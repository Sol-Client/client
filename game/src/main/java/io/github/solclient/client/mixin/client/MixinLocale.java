package io.github.solclient.client.mixin.client;

import java.io.IOException;
import java.util.*;

import org.spongepowered.asm.mixin.*;

import net.minecraft.client.resources.*;
import net.minecraft.client.resources.Locale;
import net.minecraft.util.ResourceLocation;

@Mixin(Locale.class)
public abstract class MixinLocale {

	@Overwrite
	public synchronized void loadLocaleDataFiles(IResourceManager resourceManager, List<String> languageCodes) {
		properties.clear();

		for (String language : languageCodes) {
			String vanillaLang = String.format("lang/%s.lang", language);
			String ofLang = String.format("optifine/lang/%s.lang", language);

			for (String domain : resourceManager.getResourceDomains()) {
				try {
					loadLocaleData(resourceManager.getAllResources(new ResourceLocation(domain, vanillaLang)));
					loadLocaleData(resourceManager.getAllResources(new ResourceLocation(domain, ofLang)));
				} catch (IOException ignored) {
				}
			}
		}

		checkUnicode();
	}

	@Shadow
	protected abstract void loadLocaleData(List<IResource> resourcesList) throws IOException;

	@Shadow
	private Map<String, String> properties;

	@Shadow
	protected abstract void checkUnicode();

}
