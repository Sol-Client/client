package io.github.solclient.client.v1_19_2.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import io.github.solclient.client.Client;
import io.github.solclient.client.Constants;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

	@Inject(method = "getWindowTitle", at = @At("HEAD"), cancellable = true)
	public void setTitle(CallbackInfoReturnable<String> callback) {
		callback.setReturnValue(Constants.NAME + " | Minecraft " + SharedConstants.getGameVersion().getName());
	}

	@Inject(method = "<init>", at = @At(value = "FIELD",
			target = "instance:Lnet/minecraft/client/option/GameOptions;", shift = Shift.BEFORE))
	private void init(CallbackInfo callback) {
		Client.INSTANCE.init();
	}

}
