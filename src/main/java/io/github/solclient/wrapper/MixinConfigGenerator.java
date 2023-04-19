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
		obj.addProperty("compatibilityLevel", "JAVA_8");
		obj.addProperty("minVersion", "0.8.5");

		JsonObject injectors = new JsonObject();
		injectors.addProperty("defaultRequire", 1);
		obj.add("injectors", injectors);

		int chop = path.length() + 1;
		JsonArray mixins = new JsonArray();
		ClassWrapper.instance.walkPackageTree(path, mixin -> mixins.add(mixin.substring(chop)));
		obj.add("mixins", mixins);

		return Optional.of(new ByteArrayInputStream(obj.toString().getBytes(StandardCharsets.UTF_8)));
	}

}
