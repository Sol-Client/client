package me.mcblueparrot.client.extension;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;

import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import me.mcblueparrot.client.mod.Mod;
import me.mcblueparrot.client.mod.ModCategory;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

public abstract class Extension extends Mod {

	protected File dataFolder;
	protected File configFile;
	private LoadedExtension loadedExtension;

	@Override
	public ModCategory getCategory() {
		return ModCategory.EXTENSIONS;
	}

	void setLoadedExtension(LoadedExtension extension) {
		loadedExtension = extension;
		dataFolder = new File(ExtensionManager.INSTANCE.getFolder(), getId());
		dataFolder.mkdirs();
		configFile = new File(dataFolder, "config.json");
	}

	@Override
	public String getName() {
		return loadedExtension.getName() == null ? getId() : loadedExtension.getName();
	}

	@Override
	public String getId() {
		return loadedExtension.getId();
	}

	public String getVersion() {
		return loadedExtension.getVersion();
	}

	@Override
	public String getBy() {
		return loadedExtension.getBy();
	}

	@Override
	public String getDescription() {
		return loadedExtension.getDescription() == null ? I18n.format("sol_client.extesion.default_description")
				: loadedExtension.getDescription();
	}

	@Override
	public ResourceLocation getIconLocation() {
		return loadedExtension.getIconLocation();
	}

	// Extensions should be enabled out-of-the-box.
	@Override
	public boolean isEnabledByDefault() {
		return true;
	}

	@Override
	public void loadStorage() throws IOException {
		if(configFile.exists()) {
			fromJsonObject(JsonParser.parseString(FileUtils.readFileToString(configFile, StandardCharsets.UTF_8)).getAsJsonObject());
		}
	}

	@Override
	public void saveStorage() throws IOException {
		FileUtils.writeStringToFile(configFile, toJsonObject().toString(), StandardCharsets.UTF_8);
	}

}
