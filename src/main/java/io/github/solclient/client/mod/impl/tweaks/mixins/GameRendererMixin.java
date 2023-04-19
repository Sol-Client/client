package io.github.solclient.client.mod.impl.tweaks.mixins;

import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;

import com.mojang.blaze3d.platform.GlStateManager;

import io.github.solclient.client.mod.impl.tweaks.TweaksMod;
import net.minecraft.client.render.GameRenderer;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {

	@Redirect(method = "setupCamera", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/GameRenderer;bobView(F)V"))
	public void cancelWorldBobbing(GameRenderer instance, float tickDelta) {
		if (TweaksMod.enabled && TweaksMod.instance.minimalViewBobbing)
			return;

		bobView(tickDelta);
	}

	@Redirect(method = "setupCamera", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/GameRenderer;bobViewWhenHurt(F)V"))
	public void cancelWorldRotation(GameRenderer instance, float tickDelta) {
		if (TweaksMod.enabled && TweaksMod.instance.minimalDamageShake)
			return;

		bobViewWhenHurt(tickDelta);
	}

	@Redirect(method = "bobViewWhenHurt", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/GlStateManager;rotate(FFFF)V"))
	public void adjustRotation(float angle, float x, float y, float z) {
		if (TweaksMod.enabled)
			angle *= TweaksMod.instance.getDamageShakeIntensity();

		GlStateManager.rotate(angle, x, y, z);
	}

	@Shadow
	protected abstract void bobView(float partialTicks);

	@Shadow
	protected abstract void bobViewWhenHurt(float partialTicks);

}
