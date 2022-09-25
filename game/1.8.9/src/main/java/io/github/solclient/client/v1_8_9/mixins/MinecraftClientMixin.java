package io.github.solclient.client.v1_8_9.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.solclient.client.Client;
import io.github.solclient.client.Constants;
import net.minecraft.client.MinecraftClient;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

	@ModifyConstant(method = "setPixelFormat", constant = @Constant(stringValue = "Minecraft 1.8.9"))
	public String getTitle(String title) {
		return Constants.NAME + " | " + title;
	}

	@Inject(method = "initializeGame", at = @At(value = "FIELD",
			target = "Lnet/minecraft/client/MinecraftClient;instance:Lnet/minecraft/client/option/GameOptions;", shift = Shift.BEFORE))
	public void init(CallbackInfo callback) {
		Client.INSTANCE.init();
	}

}
