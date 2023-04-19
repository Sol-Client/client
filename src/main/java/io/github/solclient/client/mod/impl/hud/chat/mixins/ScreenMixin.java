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

package io.github.solclient.client.mod.impl.hud.chat.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

import io.github.solclient.client.mod.impl.hud.chat.ChatMod;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.GameOptions;

@Mixin(Screen.class)
public class ScreenMixin {

	@Redirect(method = "handleTextClick", at = @At(value = "FIELD", target = "Lnet/minecraft/client/option/GameOptions;chatLink:Z"))
	public boolean overrideChatLinks(GameOptions settings) {
		if (ChatMod.enabled)
			return ChatMod.instance.links;

		return settings.chatLink;
	}

	@Redirect(method = "handleTextClick", at = @At(value = "FIELD", target = "Lnet/minecraft/client/option/GameOptions;chatLinkPrompt:Z"))
	public boolean overrideChatLinkPrompt(GameOptions settings) {
		if (ChatMod.enabled)
			return ChatMod.instance.promptLinks;

		return settings.chatLinkPrompt;
	}

}