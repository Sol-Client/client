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

import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.solclient.client.extension.MinecraftClientExtension;
import io.github.solclient.client.mod.impl.tweaks.TweaksMod;
import net.minecraft.client.gui.screen.*;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.resource.language.I18n;

@Mixin(DisconnectedScreen.class)
public class DisconnectedScreenMixin extends Screen {

	@Inject(method = "init", at = @At("RETURN"))
	public void postInit(CallbackInfo callback) {
		if (!(TweaksMod.enabled && TweaksMod.instance.reconnectButton))
			return;

		// whaat
		if (buttons.isEmpty())
			return;

		ButtonWidget last = buttons.get(buttons.size() - 1);
		int y = last.y;
		last.y += 24;
		buttons.add(new ButtonWidget(100, last.x, y, I18n.translate("sol_client.mod.tweaks.reconnect")));
	}

	@Inject(method = "buttonClicked", at = @At("HEAD"))
	public void reconnect(ButtonWidget button, CallbackInfo callback) {
		if (button.id != 100)
			return;

		ServerInfo server = ((MinecraftClientExtension) client).getPreviousServer();
		if (server == null)
			return;

		client.setScreen(new ConnectScreen(parent, client, server));
	}

	@Shadow
	private @Final Screen parent;

}
