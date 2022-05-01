package me.mcblueparrot.client.extension;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.AllArgsConstructor;
import lombok.Data;
import me.mcblueparrot.client.Client;
import me.mcblueparrot.client.mod.Mod;

@Data
@AllArgsConstructor
public class LoadedExtension {

	private final String fileName;
	private final String name;
	private final String id;
	private final String description;
	private final String version;
	private final String by;
	private final ClassLoader loader;
	private final String modClass;
	private final String mixinConfig;
	private Map<Integer, ResourceLocation> icons;
	private ResourceLocation defaultIcon;

	public static LoadedExtension from(String fileName, URLClassLoader loader) throws InvalidExtensionException {
		InputStream configInput = loader.getResourceAsStream("extension.json");

		if(configInput == null) {
			throw new InvalidExtensionException("extension.json is not present");
		}

		JsonElement elem = JsonParser.parseReader(new InputStreamReader(configInput, StandardCharsets.UTF_8));

		if(!elem.isJsonObject()) {
			throw new InvalidExtensionException("extension.json must be a JSON object");
		}

		JsonObject obj = elem.getAsJsonObject();

		if(!(obj.has("modClass") && obj.has("id"))) {
			throw new InvalidExtensionException("Mod class is not present in extension.json");
		}

		Map<Integer, ResourceLocation> icons = new HashMap<>();
		ResourceLocation defaultIcon = null;

		if(obj.has("icons")) {
			for(Map.Entry<String, JsonElement> entry : obj.get("icons").getAsJsonObject().entrySet()) {
				try {
					int res = Integer.parseInt(entry.getKey());

					if(res % 16 != 0 || res == 0) {
						continue;
					}

					int scale = res / 16;

					icons.put(scale, new ResourceLocation(entry.getValue().getAsString()));
				}
				catch(NumberFormatException error) {
					if(entry.getKey().equals("default")) {
						defaultIcon = new ResourceLocation(entry.getValue().getAsString());
					}
				}
			}
		}

		return new LoadedExtension(fileName, obj.has("name") ? obj.get("name").getAsString() : null,
				obj.get("id").getAsString(), obj.has("description") ? obj.get("description").getAsString() : null,
				obj.has("version") ? obj.get("version").getAsString() : null,
				obj.has("by") ? obj.get("by").getAsString() : null,
				loader, obj.get("modClass").getAsString(),
				obj.has("mixinConfig") ? obj.get("mixinConfig").getAsString() : null,
				icons, defaultIcon);
	}

	public void registerMod() throws InvalidExtensionException {
		try {
			Class<?> clazz = Class.forName(modClass, true, loader);
			try {
				Constructor<?> constructor = clazz.getConstructor();

				Extension extension = (Extension) constructor.newInstance();
				extension.setLoadedExtension(this);
				Client.INSTANCE.register(extension);
			}
			catch(NoSuchMethodException | IllegalAccessException error) {
				throw new InvalidExtensionException("Could not find single-argument constructor in " + modClass, error);
			}
			catch(InstantiationException | IllegalArgumentException | InvocationTargetException error) {
				throw new InvalidExtensionException("Could not initialise mod class " + modClass, error);
			}
		}
		catch(ClassNotFoundException error) {
			throw new InvalidExtensionException("Could not find mod class " + modClass, error);
		}
	}

	public ResourceLocation getIconLocation() {
		return getIcons().getOrDefault(new ScaledResolution(Minecraft.getMinecraft())
				.getScaleFactor(), defaultIcon);
	}
}
