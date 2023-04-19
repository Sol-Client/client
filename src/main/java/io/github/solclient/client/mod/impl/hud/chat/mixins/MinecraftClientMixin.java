package io.github.solclient.client.mod.impl.hud.chat.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

import io.github.solclient.client.mod.impl.hud.chat.ChatMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;
import net.minecraft.entity.player.PlayerEntity.ChatVisibilityType;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

	@Redirect(method = "tick", at = @At(value = "FIELD", target = "Lnet/minecraft/client/option/GameOptions;chatVisibilityType:Lnet/minecraft/entity/player/PlayerEntity$ChatVisibilityType;"))
	public ChatVisibilityType overrideChatVisibility(GameOptions instance) {
		if (ChatMod.enabled)
			return ChatVisibilityType.FULL; /* Always allow chat to be opened */

		return instance.chatVisibilityType;
	}

}
