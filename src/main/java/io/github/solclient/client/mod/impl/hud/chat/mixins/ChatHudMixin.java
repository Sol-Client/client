package io.github.solclient.client.mod.impl.hud.chat.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import io.github.solclient.client.mod.impl.hud.chat.ChatMod;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.option.GameOptions;

@Mixin(ChatHud.class)
public class ChatHudMixin {

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
