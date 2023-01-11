package io.github.solclient.client.mixin.client;

import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import io.github.solclient.client.Client;
import io.github.solclient.client.event.impl.GammaEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;

@Pseudo
@Mixin(targets = "net.optifine.LightMap")
public class MixinLightMap {

	@Inject(method = "updateLightmap", at = @At("HEAD"), cancellable = true)
	public void overrideGamma(World world, float torchFlickerX, int[] lmColors, boolean nightVision,
			CallbackInfoReturnable<Boolean> callback) {
		GammaEvent event = new GammaEvent(Minecraft.getMinecraft().gameSettings.gammaSetting);
		Client.INSTANCE.getEvents().post(event);
		if (event.gamma > 1) {
			callback.setReturnValue(false);
		}
	}

}
