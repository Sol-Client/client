package me.mcblueparrot.client.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import me.mcblueparrot.client.Client;
import me.mcblueparrot.client.event.impl.GammaEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

@Pseudo
@Mixin(targets = "net.optifine.LightMap")
public class MixinLightMap {

	@Inject(method = "updateLightmap", at = @At("HEAD"), cancellable = true)
	public void overrideGamma(World world, float torchFlickerX, int[] lmColors, boolean nightVision,
			CallbackInfoReturnable<Boolean> callback) {
		GammaEvent event = new GammaEvent(Minecraft.getMinecraft().gameSettings.gammaSetting);
		Client.INSTANCE.bus.post(event);
		if(event.gamma > 1) {
			callback.setReturnValue(false);
		}
	}

}
