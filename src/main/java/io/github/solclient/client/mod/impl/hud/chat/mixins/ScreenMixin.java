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