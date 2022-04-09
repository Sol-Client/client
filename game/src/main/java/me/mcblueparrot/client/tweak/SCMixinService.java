package me.mcblueparrot.client.tweak;

import java.io.InputStream;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.List;

import me.mcblueparrot.client.extension.LoadedExtension;
import org.spongepowered.asm.service.mojang.MixinServiceLaunchWrapper;

import me.mcblueparrot.client.extension.ExtensionManager;
import net.minecraft.launchwrapper.Launch;

public class SCMixinService extends MixinServiceLaunchWrapper {

	@Override
	public String getName() {
		return "Sol Client";
	}

	@Override
	public boolean isValid() {
		return true;
	}

	@Override
	public InputStream getResourceAsStream(String name) {
		InputStream superStream = super.getResourceAsStream(name);

		if(superStream != null) {
			return superStream;
		}

		for(LoadedExtension extension : ExtensionManager.INSTANCE.getExtensions()) {
			InputStream fallbackInput = extension.getLoader().getResourceAsStream(name);

			if(fallbackInput != null) {
				return fallbackInput;
			}
		}

		return null;
	}

}
