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

package io.github.solclient.client.mod.impl.core.mixins.client;

import java.util.*;

import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

import io.github.solclient.client.SolClient;
import io.github.solclient.client.mod.*;
import io.github.solclient.client.mod.impl.StandardMod;
import net.minecraft.client.resource.FallbackResourceManager;
import net.minecraft.resource.*;

@Mixin(ReloadableResourceManagerImpl.class)
public class SimpleReloadableResourceManagerMixin {

	@Inject(method = "reload", at = @At(value = "INVOKE", target = "Lnet/minecraft/resource/ReloadableResourceManagerImpl;notifyListeners()V", shift = At.Shift.BEFORE))
	public void injectDomains(List<ResourcePack> packs, CallbackInfo callback) {
		injectNamespace("replaymod");
		injectNamespace("jgui");
		injectNamespace("sol_client");

		if (!SolClient.INSTANCE.getMods().isEmpty()) {
			for (Mod mod : SolClient.INSTANCE)
				if (!(mod instanceof StandardMod))
					injectNamespace(mod.getId());
		} else
			for (ModInfo info : SolClient.INSTANCE.getQueue())
				injectNamespace(info.getId());
	}

	private void injectNamespace(String namespace) {
		namespaces.add(namespace);
		fallbackManagers.put(namespace, fallbackManagers.get("minecraft"));
	}

	@Final
	@Shadow
	private Set<String> namespaces;

	@Final
	@Shadow
	private Map<String, FallbackResourceManager> fallbackManagers;

}
