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

import java.io.File;
import java.util.*;

import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.option.GameOptions;
import net.minecraft.client.resource.ResourcePackLoader;
import net.minecraft.resource.ResourcePack;
import net.minecraft.util.MetadataSerializer;

@Mixin(ResourcePackLoader.class)
public class ResourcePackLoaderMixin {

	@Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Ljava/lang/String;equals(Ljava/lang/Object;)Z"))
	public boolean skipIt(String instance, Object other) {
		return false;
	}

	@Inject(method = "<init>", at = @At("RETURN"))
	public void postInit(File resourcePackDir, File serverResourcePackDir, ResourcePack defaultPack,
			MetadataSerializer serializer, GameOptions gameOptions, CallbackInfo callback) {
		Map<File, ResourcePackLoader> loaders = new HashMap<>();

		Iterator<String> iterator = gameOptions.resourcePacks.iterator();

		while (iterator.hasNext()) {
			String packName = iterator.next();
			File file = new File(resourcePackDir, packName);

			if (!file.exists()) {
				continue;
			}

			File parent = file.getParentFile();

			ResourcePackLoader applicableLoader;

			if (packName.indexOf('/') != -1)
				applicableLoader = loaders.computeIfAbsent(parent, (ignored) -> new ResourcePackLoader(parent,
						serverResourcePackDir, defaultPack, serializer, gameOptions));
			else
				applicableLoader = (ResourcePackLoader) (Object) this;

			for (ResourcePackLoader.Entry entry : applicableLoader.getAvailableResourcePacks()) {
				if (entry.getName().equals(packName)) {
					if (entry.getFormat() == 1 || gameOptions.incompatibleResourcePacks.contains(entry.getName())) {
						selectedResourcePacks.add(entry);
						break;
					}

					iterator.remove();
					LOGGER.warn("Removed selected resource pack {} because it\'s no longer compatible",
							entry.getName());
				}
			}
		}
	}

	@Shadow
	private static @Final Logger LOGGER;

	@Shadow
	private List<ResourcePackLoader.Entry> selectedResourcePacks;

}
