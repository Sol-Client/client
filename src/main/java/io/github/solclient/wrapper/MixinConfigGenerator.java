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

package io.github.solclient.wrapper;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

import com.google.gson.*;

import lombok.experimental.UtilityClass;

@UtilityClass
public class MixinConfigGenerator {

	public static final String PREFIX = "generated/mixin/";

	public Optional<InputStream> generate(String path) throws IOException {
		if (!path.startsWith(PREFIX))
			return Optional.empty();

		path = path.substring(PREFIX.length());
		if (path.isEmpty())
			return Optional.empty();

		// groan

		JsonObject obj = new JsonObject();
		obj.addProperty("required", true);
		obj.addProperty("package", path);
		obj.addProperty("refmap", "sol-client-wrapper-refmap.json");
		obj.addProperty("compatibilityLevel", "JAVA_8");
		obj.addProperty("minVersion", "0.8.5");

		JsonObject injectors = new JsonObject();
		injectors.addProperty("defaultRequire", 1);
		obj.add("injectors", injectors);

		int chop = path.length() + 1;
		JsonArray mixins = new JsonArray();
		JarIndex.getPackageChildren(path).forEach(mixin -> mixins.add(new JsonPrimitive(mixin.substring(chop))));
		obj.add("mixins", mixins);

		return Optional.of(new ByteArrayInputStream(obj.toString().getBytes(StandardCharsets.UTF_8)));
	}

}
