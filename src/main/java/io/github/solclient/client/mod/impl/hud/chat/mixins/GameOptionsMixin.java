package io.github.solclient.client.mod.impl.hud.chat.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

import io.github.solclient.client.mod.impl.hud.chat.ChatMod;
import net.minecraft.client.option.GameOptions;

@Mixin(GameOptions.class)
public class GameOptionsMixin {

	@Redirect(method = "onPlayerModelPartChange", at = @At(value = "FIELD", target = "Lnet/minecraft/client/option/GameOptions;chatColor:Z"))
	public boolean overrideChatColours(GameOptions settings) {
		if (ChatMod.enabled)
			return ChatMod.instance.colours;

		return settings.chatColor;
	}

}