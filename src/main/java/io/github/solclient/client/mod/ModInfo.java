/*
 * Sol Client - an open source Minecraft client
 * Copyright (C) 2021-2023  TheKodeToad and Contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package io.github.solclient.client.mod;

import java.io.*;
import java.lang.reflect.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.zip.*;

import com.google.gson.*;

import io.github.solclient.wrapper.ClassWrapper;
import lombok.Data;

@Data
public final class ModInfo {

	// yes hack
	static ModInfo inject;
	private final Optional<Path> path;
	private final String id;
	private final String main;
	private final Optional<String> version;
	private final Optional<String> name, description;
	private final ModCategory category;
	private final boolean enabledByDefault, forcedOn;
	private final List<String> mixins;

	public static ModInfo parse(Path path) throws InvalidModException, IOException {
		try (ZipFile zip = new ZipFile(path.toFile())) {
			ZipEntry entry = zip.getEntry("addon.json");
			if (entry == null)
				entry = zip.getEntry("sol-client-addon.json");
			if (entry == null)
				throw new InvalidModException("No addon or sol-client-addon.json found");

			try (Reader reader = new InputStreamReader(zip.getInputStream(entry))) {
				return parse(path, reader);
			}
		}
	}

	public static ModInfo parseFromClasspath() throws InvalidModException, IOException {
		ClassWrapper wrapper = ClassWrapper.getInstance();
		URL url = wrapper.getResource("addon.json");
		if (url == null)
			url = wrapper.getResource("sol-client.addon.json");
		if (url == null)
			throw new InvalidModException("No addon or sol-client-addon.json found in resources");

		try (Reader reader = new InputStreamReader(url.openStream(), StandardCharsets.UTF_8)) {
			return parse(null, reader);
		}
	}

	public static ModInfo parse(Path path, Reader reader) throws InvalidModException {
		JsonElement element = new JsonParser().parse(reader);
		if (!element.isJsonObject())
			throw new InvalidModException("Mod description must be an object");

		JsonObject object = element.getAsJsonObject();
		return parse(path, null, object);
	}

	private static String prefixMixin(String classable, String prefix) {
		if (prefix == null)
			return classable;
		if (!classable.startsWith("@"))
			return classable;
		return '@' + prefix + '.' + classable.substring(1);
	}

	public static ModInfo parse(Path path, String prefix, JsonObject object) throws InvalidModException {
		if (!(object.has("id") && object.get("id").isJsonPrimitive()))
			throw new InvalidModException("Mod description must contain string 'id'");
		String id = object.get("id").getAsString();

		if (!(object.has("main") && object.get("main").isJsonPrimitive()))
			throw new InvalidModException("Mod description must contain string 'main'");
		String main = object.get("main").getAsString();
		if (prefix != null)
			main = prefix + '.' + main;

		Optional<String> version = Optional.empty();
		if (object.has("version") && object.get("version").isJsonPrimitive())
			version = Optional.of(object.get("version").getAsString());

		Optional<String> name = Optional.empty();
		if (object.has("name") && object.get("name").isJsonPrimitive())
			name = Optional.of(object.get("name").getAsString());

		Optional<String> description = Optional.empty();
		if (object.has("description") && object.get("description").isJsonPrimitive())
			description = Optional.of(object.get("description").getAsString());

		List<String> mixins = Collections.emptyList();
		if (object.has("mixins")) {
			JsonElement mixinsElement = object.get("mixins");
			if (mixinsElement.isJsonArray()) {
				mixins = new ArrayList<>();
				for (JsonElement mixin : mixinsElement.getAsJsonArray())
					mixins.add(prefixMixin(mixin.getAsString(), prefix));
			} else if (mixinsElement.isJsonPrimitive())
				mixins = Arrays.asList(prefixMixin(mixinsElement.getAsString(), prefix));
		}

		ModCategory category = ModCategory.INSTALLED;
		if (object.has("category") && object.get("category").isJsonPrimitive())
			category = ModCategory.getByName(object.get("category").getAsString());

		boolean enabledByDefault = false;
		if (object.has("enabledByDefault") && object.get("enabledByDefault").isJsonPrimitive())
			enabledByDefault = object.get("enabledByDefault").getAsBoolean();

		boolean forcedOn = false;
		if (object.has("forcedOn") && object.get("forcedOn").isJsonPrimitive())
			forcedOn = object.get("forcedOn").getAsBoolean();

		return new ModInfo(Optional.ofNullable(path), id, main, version, name, description, category, enabledByDefault,
				forcedOn, mixins);
	}

	public Mod construct() throws NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException, ClassNotFoundException {
		Class<?> clazz = ClassWrapper.getInstance().loadClass(main);

		// special method
		try {
			Method instanceMethod = clazz.getMethod("findModInstance");
			if (Mod.class.isAssignableFrom(instanceMethod.getReturnType()))
				return (Mod) instanceMethod.invoke(null);
		} catch (NoSuchMethodException ignored) {
		}

		Constructor<?> constructor = clazz.getConstructor();
		inject = this;
		Mod mod = (Mod) constructor.newInstance();
		inject = null;
		return mod;
	}

	public boolean isStandard() {
		return main.startsWith("io.github.solclient.client.mod.impl.");
	}

}
