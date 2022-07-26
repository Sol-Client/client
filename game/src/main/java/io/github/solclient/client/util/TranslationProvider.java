package io.github.solclient.client.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.github.solclient.client.platform.mc.MinecraftClient;
import io.github.solclient.client.platform.mc.resource.Identifier;
import io.github.solclient.client.platform.mc.resource.Resource;
import io.github.solclient.client.platform.mc.resource.ResourceManager;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TranslationProvider {

	private static final Map<String, String> TRANSLATIONS = new HashMap<>();

	public void clear() {
		TRANSLATIONS.clear();
	}

	public void accept(String jsonStr) {
		JsonObject obj = JsonParser.parseString(JsonComments.swallowComments(jsonStr)).getAsJsonObject();
		for(Map.Entry<String, JsonElement> entry : obj.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue().getAsString();
			TRANSLATIONS.put(key, value);
		}
	}

	public String translate(String key) {
		return TRANSLATIONS.getOrDefault(key, "sol_client." + key);
	}

	public static Boolean hasTranslation(String key) {
		return TRANSLATIONS.containsKey(key);
	}

	public static Identifier getLanguageId(String id) {
		return Identifier.solClient("lang/" + id + ".json");
	}

	public static void load() {
		clear();

		try {
			MinecraftClient mc = MinecraftClient.getInstance();
			ResourceManager resourceManager = mc.getResourceManager();

			List<Resource> resources = new ArrayList<>();

			resources.addAll(resourceManager.getResources(Identifier.solClient("lang/en_us.json")));
			resources.addAll(resourceManager.getResources(Identifier.solClient("lang/" + mc.getOptions().languageCode() + ".json")));

			for(Resource resource : resources) {
				try(InputStream in = resource.getInput()) {
					accept(IOUtils.toString(in, StandardCharsets.UTF_8));
				}
			}
		}
		catch(IOException ignored) {
		}
	}

}
