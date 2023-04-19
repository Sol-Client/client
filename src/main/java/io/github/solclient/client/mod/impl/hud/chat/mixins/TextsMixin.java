package io.github.solclient.client.mod.impl.hud.chat.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import io.github.solclient.client.mod.impl.hud.chat.ChatMod;
import net.minecraft.client.util.Texts;

@Mixin(Texts.class)
public class TextsMixin {

	@Inject(method = "getRenderChatMessage", at = @At("HEAD"), cancellable = true)
	private static void overrideChatColours(String input, boolean defaultValue,
			CallbackInfoReturnable<String> callback) {
		if (ChatMod.enabled)
			callback.setReturnValue(input);
	}

}