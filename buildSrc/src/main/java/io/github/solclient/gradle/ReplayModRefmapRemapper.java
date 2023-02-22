package io.github.solclient.gradle;

import java.util.Map;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

public final class ReplayModRefmapRemapper {

	private ReplayModClassRemapper remapper;

	public ReplayModRefmapRemapper(ReplayModClassRemapper remapper) {
		this.remapper = remapper;
	}

	public String remap(String data) {
		JsonObject obj = JsonParser.parseString(data).getAsJsonObject();
		JsonObject mappings = obj.get("mappings").getAsJsonObject();
		for (Map.Entry<String, JsonElement> mixinClassEntry : mappings.entrySet()) {
			JsonObject mixinClass = mixinClassEntry.getValue().getAsJsonObject();
			for (Map.Entry<String, JsonElement> entry : mixinClass.entrySet()) {
				String value = entry.getValue().getAsString();
				boolean field = value.indexOf(':') != -1;

				String owner = null;
				int nameStart = 0;

				if (value.startsWith("L")) {
					nameStart = value.indexOf(';') + 1;
					owner = value.substring(1, nameStart - 1);
				}

				String name;
				int descStart;

				if (field) {
					// field: desc starts after colon
					descStart = value.indexOf(':') + 1;
					name = value.substring(nameStart, descStart - 1);
				} else {
					// method: desc starts from opening parenthesis
					descStart = value.indexOf('(');
					name = value.substring(nameStart, descStart);
				}

				String desc = value.substring(descStart);

				name = field ? remapper.mapFieldName(owner, name, desc) : remapper.mapMethodName(owner, name, desc);
				desc = remapper.mapDesc(desc);

				if (owner != null)
					owner = remapper.map(owner);

				StringBuilder result = new StringBuilder();

				if (owner != null)
					result.append('L').append(owner).append(';');

				result.append(name);
				if (field)
					result.append(':');
				result.append(desc);

				entry.setValue(new JsonPrimitive(result.toString()));
			}
		}

		data = obj.toString();
		return data;
	}

}
