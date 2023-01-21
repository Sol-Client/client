package io.github.solclient.client.mixin.client;

import java.io.IOException;
import java.util.*;

import org.spongepowered.asm.mixin.*;

import net.minecraft.client.resource.language.TranslationStorage;
import net.minecraft.resource.*;
import net.minecraft.util.Identifier;

@Mixin(TranslationStorage.class)
public abstract class MixinTranslationStorage {

	@Overwrite
	public synchronized void load(ResourceManager resourceManager, List<String> languageCodes) {
		translations.clear();

		for (String language : languageCodes) {
			String vanillaLang = String.format("lang/%s.lang", language);
			String ofLang = String.format("optifine/lang/%s.lang", language);

			for (String domain : resourceManager.getAllNamespaces()) {
				try {
					load(resourceManager.getAllResources(new Identifier(domain, vanillaLang)));
					load(resourceManager.getAllResources(new Identifier(domain, ofLang)));
				} catch (IOException ignored) {
				}
			}
		}

		setRightToLeft();
	}

	@Shadow
	protected abstract void load(List<Resource> resourcesList) throws IOException;

	@Shadow
	Map<String, String> translations;

	@Shadow
	protected abstract void setRightToLeft();

}
