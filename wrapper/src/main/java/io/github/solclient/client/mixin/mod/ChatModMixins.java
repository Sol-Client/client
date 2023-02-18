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

package io.github.solclient.client.mixin.mod;

import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

import io.github.solclient.client.extension.ScreenExtension;
import io.github.solclient.client.mod.impl.hud.chat.*;
import io.github.solclient.client.ui.screen.mods.ModsScreen;
import lombok.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.*;
import net.minecraft.client.gui.screen.*;
import net.minecraft.client.gui.screen.options.ChatOptionsScreen;
import net.minecraft.client.gui.widget.*;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.Texts;
import net.minecraft.entity.player.PlayerEntity.ChatVisibilityType;

public class ChatModMixins {

	@Mixin(Screen.class)
	public static class ScreenMixin {

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

	@Mixin(GameOptions.class)
	public static class GameOptionsMixin {

		@Redirect(method = "onPlayerModelPartChange", at = @At(value = "FIELD", target = "Lnet/minecraft/client/option/GameOptions;chatColor:Z"))
		public boolean overrideChatColours(GameOptions settings) {
			if (ChatMod.enabled)
				return ChatMod.instance.colours;

			return settings.chatColor;
		}

	}

	@Mixin(Texts.class)
	public static class TextsMixin {

		@Inject(method = "getRenderChatMessage", at = @At("HEAD"), cancellable = true)
		private static void overrideChatColours(String input, boolean defaultValue,
				CallbackInfoReturnable<String> callback) {
			if (ChatMod.enabled)
				callback.setReturnValue(input);
		}

	}

	@Mixin(ChatScreen.class)
	public static class ChatScreenMixin implements ScreenExtension {

		@Override
		public boolean canBeForceClosed() {
			if (ChatMod.enabled)
				return !ChatMod.instance.preventClose;

			return true;
		}

	}

	@Mixin(ChatHud.class)
	public static class ChatHudMixin {

		@Inject(method = "isChatFocused", at = @At("HEAD"), cancellable = true)
		public void overrideChatOpen(CallbackInfoReturnable<Boolean> callback) {
			if (ChatMod.enabled && ChatMod.instance.peekKey.isPressed())
				callback.setReturnValue(true);
		}

		@Redirect(method = "getWidth()I", at = @At(value = "FIELD", target = "Lnet/minecraft/client/option/GameOptions;chatWidth:F"))
		public float overrideChatWidth(GameOptions instance) {
			if (ChatMod.enabled)
				return ChatMod.instance.width / 320F;

			return instance.chatWidth;
		}

		@Redirect(method = "getHeight()I", at = @At(value = "FIELD", target = "Lnet/minecraft/client/option/GameOptions;chatHeightFocused:F"))
		public float overrideOpenChatHeight(GameOptions instance) {
			if (ChatMod.enabled)
				return ChatMod.instance.openHeight / 180F;

			return instance.chatHeightFocused;
		}

		@Redirect(method = "getHeight()I", at = @At(value = "FIELD", target = "Lnet/minecraft/client/option/GameOptions;chatHeightUnfocused:F"))
		public float overrideClosedChatHeight(GameOptions instance) {
			if (ChatMod.enabled)
				return ChatMod.instance.closedHeight / 180F;

			return instance.chatHeightFocused;
		}

		@Redirect(method = "getChatScale", at = @At(value = "FIELD", target = "Lnet/minecraft/client/option/GameOptions;chatScale:F"))
		public float overrideChatScale(GameOptions instance) {
			if (ChatMod.enabled)
				return ChatMod.instance.scale / 100F;

			return instance.chatScale;
		}

	}

	@Mixin(MinecraftClient.class)
	public static class MinecraftClientMixin {

		@Redirect(method = "tick", at = @At(value = "FIELD", target = "Lnet/minecraft/client/option/GameOptions;chatVisibilityType:Lnet/minecraft/entity/player/PlayerEntity$ChatVisibilityType;"))
		public ChatVisibilityType overrideChatVisibility(GameOptions instance) {
			if (ChatMod.enabled)
				return ChatVisibilityType.FULL; /* Always allow chat to be opened */

			return instance.chatVisibilityType;
		}

	}

	@Mixin(ChatOptionsScreen.class)
	public static class ChatOptionsScreenMixin extends Screen {

		@Shadow
		private @Final GameOptions options;

		@Inject(method = "init", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/screen/options/ChatOptionsScreen;title:Ljava/lang/String;", shift = At.Shift.AFTER), cancellable = true)
		public void replaceGui(CallbackInfo callback) {
			if (ChatMod.enabled) {
				buttons.add(new OptionButtonWidget(GameOptions.Option.REDUCED_DEBUG_INFO.ordinal(),
						this.width / 2 - (150 / 2), height / 6 + 76, GameOptions.Option.REDUCED_DEBUG_INFO,
						options.getValueMessage(GameOptions.Option.REDUCED_DEBUG_INFO)));
				buttons.add(new ButtonWidget(201, width / 2 - (150 / 2), height / 6 + 98, 150, 20,
						I18n.translate("sol_client.more_options")));
				buttons.add(new ButtonWidget(200, width / 2 - (150 / 2), height / 6 + 120, 150, 20,
						I18n.translate("gui.done")));
				callback.cancel();
			}
		}

		@Inject(method = "buttonClicked", at = @At("RETURN"))
		public void buttonClicked(ButtonWidget button, CallbackInfo callback) {
			if (button.id == 201)
				client.setScreen(new ModsScreen(ChatMod.instance));
		}

	}

	@Mixin(ChatHudLine.class)
	public static class ChatHudLineMixin implements ChatAnimationData {

		@Getter
		@Setter
		private float transparency = 1;

		@Getter
		@Setter
		private float lastTransparency = 1;

	}

}
