package io.github.solclient.client.addon;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.*;

import com.google.gson.*;

import io.github.solclient.wrapper.ClassWrapper;
import lombok.Data;

@Data
public final class AddonInfo {

	private final Optional<Path> path;
	private final String id;
	private final String main;
	private final String version;
	private final Optional<String> name, description;
	private final List<String> mixins;

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
			return parse(null, reader);
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

		if (!(object.has("version") && object.get("version").isJsonPrimitive()))
			throw new InvalidAddonException("Addon description must contain string 'version'");
		String version = object.get("version").getAsString();

		Optional<String> name = Optional.empty();
		if (object.has("name") && object.get("name").isJsonPrimitive())
			name = Optional.of(object.get("name").getAsString());

		Optional<String> description = Optional.empty();
		if (object.has("description") && object.get("description").isJsonPrimitive())
			description = Optional.of(object.get("description").getAsString());

		List<String> mixins = Collections.emptyList();
		if (object.has("mixins")) {
			JsonElement mixinsElement = object.get("mixins");
			if (mixinsElement.isJsonArray())
				mixins = mixinsElement.getAsJsonArray().asList().stream().map(JsonElement::getAsString).collect(Collectors.toList());
			else if (mixinsElement.isJsonPrimitive())
				mixins = Arrays.asList(mixinsElement.getAsString());
		}

		return new AddonInfo(Optional.ofNullable(path), id, main, version, name, description, mixins);
	}

}
