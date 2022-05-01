package me.mcblueparrot.client.extension;

import me.mcblueparrot.client.mod.Mod;
import me.mcblueparrot.client.mod.ModCategory;
import net.minecraft.util.ResourceLocation;

public abstract class Extension extends Mod {

	private LoadedExtension loadedExtension;

	@Override
	public ModCategory getCategory() {
		return ModCategory.EXTENSIONS;
	}

	void setLoadedExtension(LoadedExtension extension) {
		loadedExtension = extension;
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
		return loadedExtension.getDescription() == null ? "No description provided." : loadedExtension.getDescription();
	}

	@Override
	public ResourceLocation getIconLocation() {
		return loadedExtension.getIconLocation();
	}

}
