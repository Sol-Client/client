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

import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.screen.ingame.HandledScreen;

/**
 * @reason Allow mouse hotkeys in container GUI.
 */
@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin {

	@Inject(method = "mouseClicked(III)V", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/screen/ingame/HandledScreen;cancelNextRelease:Z", ordinal = 0, shift = At.Shift.AFTER), cancellable = true)
	public void allowMouseInput(int mouseX, int mouseY, int mouseButton, CallbackInfo callback) {
		if (handleHotbarKeyPressed(mouseButton - 100))
			callback.cancel();
	}

	@Shadow
	protected abstract boolean handleHotbarKeyPressed(int keyCode);

}
