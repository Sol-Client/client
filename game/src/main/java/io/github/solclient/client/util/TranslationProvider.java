package io.github.solclient.client.util;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.experimental.UtilityClass;

@UtilityClass
public class TranslationProvider {

	private Map<String, String> translations = new HashMap<>();

	public void clear() {
		translations.clear();
	}

	public void accept(String jsonStr) {
		JsonObject obj = JsonParser.parseString(JsonComments.swallowComments(jsonStr)).getAsJsonObject();
		for(Map.Entry<String, JsonElement> entry : obj.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue().getAsString();
			translations.put(key, value);
		}
	}

	public String translate(String str) {
		return translations.getOrDefault(str, "sol_client." + str);
	}

	public String translate(String str, Object... args) {
		if(args.length == 0) {
			return translate(str);
		}

		return String.format(translate(str), args);
	}

}
