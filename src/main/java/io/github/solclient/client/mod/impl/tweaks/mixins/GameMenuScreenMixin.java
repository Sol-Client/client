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

package io.github.solclient.client.mod.impl.tweaks.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.solclient.client.mod.impl.tweaks.TweaksMod;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.Formatting;

@Mixin(GameMenuScreen.class)
public class GameMenuScreenMixin {

	private boolean disconnect;

	@Inject(method = "init", at = @At("HEAD"))
	public void init(CallbackInfo callback) {
		disconnect = !isConfirmEnabled();
	}

	@Inject(method = "buttonClicked", at = @At("HEAD"), cancellable = true)
	public void overrideButton(ButtonWidget button, CallbackInfo callback) {
		if (button.id == 1 && !disconnect) {
			callback.cancel();
			button.message = Formatting.GREEN + I18n.translate("sol_client.mod.tweaks.confirm_disconnect");
			disconnect = true;
		}
	}

	private boolean isConfirmEnabled() {
		return TweaksMod.enabled && TweaksMod.instance.confirmDisconnect;
	}

}