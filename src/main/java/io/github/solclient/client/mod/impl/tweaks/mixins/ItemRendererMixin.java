package io.github.solclient.client.mod.impl.tweaks.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.platform.GlStateManager;

import io.github.solclient.client.mod.impl.tweaks.TweaksMod;
import net.minecraft.client.render.item.HeldItemRenderer;

@Mixin(HeldItemRenderer.class)
public class ItemRendererMixin {

	@Inject(method = "renderFireOverlay", at = @At("HEAD"))
	public void transformFire(float tickDelta, CallbackInfo callback) {
		if (TweaksMod.enabled && TweaksMod.instance.lowerFireBy != 0) {
			GlStateManager.pushMatrix();
			GlStateManager.translate(0, -TweaksMod.instance.lowerFireBy, 0);
		}
	}

	@Inject(method = "renderFireOverlay", at = @At("RETURN"))
	public void popFire(float tickDelta, CallbackInfo callback) {
		if (TweaksMod.enabled && TweaksMod.instance.lowerFireBy != 0)
			GlStateManager.popMatrix();
	}

}
