package io.github.solclient.client.addon;

import java.io.*;
import java.nio.file.Path;
import java.util.zip.*;

import com.google.gson.*;

import lombok.Data;

@Data
public final class AddonInfo {

	private final Path path;
	private final String name, id, main;

	static AddonInfo parse(Path path) throws InvalidAddonException, IOException {
		try (ZipFile zip = new ZipFile(path.toFile())) {
			ZipEntry entry = zip.getEntry("addon.json");
			if (entry == null)
				entry = zip.getEntry("sol-client-addon.json");
			if (entry == null)
				throw new InvalidAddonException("No addon or sol-client-addon.json found");

			try (Reader reader = new InputStreamReader(zip.getInputStream(entry))) {
				JsonElement element = JsonParser.parseReader(reader);
				if (!element.isJsonObject())
					throw new InvalidAddonException("Addon description must be an object");

				JsonObject object = element.getAsJsonObject();

				if (!(object.has("id") && object.get("id").isJsonPrimitive()))
					throw new InvalidAddonException("Addon description must contain string 'id'");
				String id = object.get("id").getAsString();

				if (!(object.has("main") && object.get("main").isJsonPrimitive()))
					throw new InvalidAddonException("Addon description must contain string 'main'");
				String main = object.get("main").getAsString();

				String name;
				if (object.has("name") && object.get("name").isJsonPrimitive())
					name = object.get("name").getAsString();
				else
					name = id;

				return new AddonInfo(path, name, id, main);
			}
		}
	}

}
