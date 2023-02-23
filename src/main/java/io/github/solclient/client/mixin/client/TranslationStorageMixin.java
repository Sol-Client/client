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

package io.github.solclient.client.mixin.client;

import java.io.IOException;
import java.util.*;

import org.spongepowered.asm.mixin.*;

import net.minecraft.client.resource.language.TranslationStorage;
import net.minecraft.resource.*;
import net.minecraft.util.Identifier;

@Mixin(TranslationStorage.class)
public abstract class TranslationStorageMixin {

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
