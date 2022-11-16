package io.github.solclient.client.v1_19_2.mixins.render;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import io.github.solclient.client.event.EventBus;
import io.github.solclient.client.event.impl.world.GammaEvent;
import net.minecraft.client.render.LightmapTextureManager;

@Mixin(LightmapTextureManager.class)
public class LightmapTextureManagerMixin {

	@ModifyExpressionValue(method = "update", at = @At(value = "INVOKE", target = "Ljava/lang/Double;floatValue()F", ordinal = 1))
	public float modifyGamma(float gamma) {
		return EventBus.DEFAULT.post(new GammaEvent(gamma)).getGamma();
	}

}
