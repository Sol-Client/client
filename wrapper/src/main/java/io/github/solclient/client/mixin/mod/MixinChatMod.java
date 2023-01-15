package io.github.solclient.client.mixin.mod;

import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

import io.github.solclient.client.mod.impl.hud.chat.*;
import io.github.solclient.client.ui.screen.mods.ModsScreen;
import io.github.solclient.client.util.extension.*;
import lombok.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.*;
import net.minecraft.client.gui.screen.*;
import net.minecraft.client.gui.screen.options.ChatOptionsScreen;
import net.minecraft.client.gui.widget.*;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.Texts;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerEntity.ChatVisibilityType;

public class MixinChatMod {

	@Mixin(Screen.class)
	public static class MixinScreen {

		@Redirect(method = "handleTextClick", at = @At(value = "FIELD", target = "Lnet/minecraft/client/option/GameOptions;chatLink"))
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
	public static class MixinGameOptions {

		@Redirect(method = "onPlayerModelPartChange", at = @At(value = "FIELD", target = "Lnet/minecraft/client/option/GameOptions;chatColor:Z"))
		public boolean overrideChatColours(GameOptions settings) {
			if (ChatMod.enabled)
				return ChatMod.instance.colours;

			return settings.chatColor;
		}

	}

	@Mixin(Texts.class)
	public static class MixinTexts {

		@Inject(method = "getRenderChatMessage", at = @At("HEAD"), cancellable = true)
		private static void overrideChatColours(String input, boolean defaultValue,
				CallbackInfoReturnable<String> callback) {
			if (ChatMod.enabled)
				callback.setReturnValue(input);
		}

	}

	@Mixin(ChatScreen.class)
	public static class MixinChatScreen implements ScreenExtension {

		@Override
		public boolean canBeForceClosed() {
			if (ChatMod.enabled)
				return !ChatMod.instance.preventClose;

			return true;
		}

	}

	@Mixin(ChatHud.class)
	public static class MixinChatHud {

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
	public static class MixinMinecraftClient {

		@Redirect(method = "tick", at = @At(value = "FIELD", target = "Lnet/minecraft/client/option/GameOptions;chatVisibilityType:Lnet/minecraft/entity/player/PlayerEntity$ChatVisibilityType;"))
		public ChatVisibilityType overrideChatVisibility(GameOptions instance) {
			if (ChatMod.enabled)
				return ChatVisibilityType.FULL; /* Always allow chat to be opened */

			return instance.chatVisibilityType;
		}

	}

	@Mixin(ChatOptionsScreen.class)
	public static class MixinChatOptionsScreen extends Screen {

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
	public static class MixinChatHudLine implements ChatAnimationData {

		@Getter
		@Setter
		private float transparency = 1;

		@Getter
		@Setter
		private float lastTransparency = 1;

	}

}
