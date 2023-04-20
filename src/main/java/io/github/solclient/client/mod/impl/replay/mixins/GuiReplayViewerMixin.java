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
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.replaymod.lib.de.johni0702.minecraft.gui.container.GuiScreen;
import com.replaymod.lib.de.johni0702.minecraft.gui.element.GuiButton;
import com.replaymod.replay.ReplayModReplay;
import com.replaymod.replay.gui.screen.GuiReplayViewer;

import io.github.solclient.client.mod.impl.replay.SCReplayMod;
import io.github.solclient.client.ui.screen.JGuiPreviousScreen;
import io.github.solclient.client.ui.screen.mods.ModsScreen;
import net.minecraft.client.MinecraftClient;

@Mixin(GuiReplayViewer.class)
public class GuiReplayViewerMixin extends GuiScreen {

	@Inject(method = "<init>", at = @At("RETURN"), remap = false)
	public void overrideSettings(ReplayModReplay mod, CallbackInfo callback) {
		MinecraftClient.getInstance().currentScreen = new JGuiPreviousScreen(this);
		settingsButton.onClick(() -> MinecraftClient.getInstance().setScreen(new ModsScreen(SCReplayMod.instance)));
	}

	@Override
	public void display() {
		if (!SCReplayMod.enabled)
			MinecraftClient.getInstance().setScreen(null);
		else
			super.display();
	}

	@Shadow
	public @Final GuiButton settingsButton;

}
