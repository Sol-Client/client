package io.github.solclient.client.addon;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.zip.*;

import com.google.gson.*;

import io.github.solclient.wrapper.ClassWrapper;
import lombok.Data;

@Data
public final class AddonInfo {

	private final Path path;
	private final String id, main, name, description;

	static AddonInfo parse(Path path) throws InvalidAddonException, IOException {
		try (ZipFile zip = new ZipFile(path.toFile())) {
			ZipEntry entry = zip.getEntry("addon.json");
			if (entry == null)
				entry = zip.getEntry("sol-client-addon.json");
			if (entry == null)
				throw new InvalidAddonException("No addon or sol-client-addon.json found");

			try (Reader reader = new InputStreamReader(zip.getInputStream(entry))) {
				return parse(path, reader);
			}
		}
	}

	static AddonInfo parseFromClasspath() throws InvalidAddonException, IOException {
		ClassWrapper wrapper = ClassWrapper.getInstance();
		URL url = wrapper.getResource("addon.json");
		if (url == null)
			url = wrapper.getResource("sol-client.addon.json");
		if (url == null)
			throw new InvalidAddonException("No addon or sol-client-addon.json found in resources");

		try (Reader reader = new InputStreamReader(url.openStream(), StandardCharsets.UTF_8)) {
			return parse(Paths.get(".."), reader);
		}
	}

	static AddonInfo parse(Path path, Reader reader) throws InvalidAddonException {
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

		String name = null;
		if (object.has("name") && object.get("name").isJsonPrimitive())
			name = object.get("name").getAsString();

		String description = null;
		if (object.has("description") && object.get("description").isJsonPrimitive())
			description = object.get("description").getAsString();

		return new AddonInfo(path, id, main, name, description);
	}

}
