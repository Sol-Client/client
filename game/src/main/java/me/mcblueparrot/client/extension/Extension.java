package me.mcblueparrot.client.extension;

import me.mcblueparrot.client.mod.Mod;
import me.mcblueparrot.client.mod.ModCategory;

public abstract class Extension extends Mod {

	private LoadedExtension loadedExtension;

	public Extension() {
		super(null, null, null, ModCategory.EXTENSIONS);
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

	@Override
	public String getDescription() {
		return loadedExtension.getDescription() == null ? "No description provided." : loadedExtension.getDescription();
	}

}
