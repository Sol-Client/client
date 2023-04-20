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

package io.github.solclient.client.mod.impl.replay.mixins;

import org.spongepowered.asm.mixin.*;

import com.replaymod.core.*;

import io.github.solclient.client.mod.impl.replay.SCReplayMod;
import io.github.solclient.client.ui.screen.mods.ModsScreen;
import net.minecraft.client.MinecraftClient;

@Mixin(ReplayMod.class)
public class ReplayModMixin {

	/**
	 * @author TheKodeToad
	 * @reason Overwrites are not always a crime.
	 */
	@Overwrite(remap = false)
	public void registerKeyBindings(KeyBindingRegistry registry) {
		registry.registerKeyBinding("replaymod.input.settings", 0,
				() -> mc.setScreen(new ModsScreen(SCReplayMod.instance)), false);
	}

	@Final
	@Shadow
	private static MinecraftClient mc;

}
