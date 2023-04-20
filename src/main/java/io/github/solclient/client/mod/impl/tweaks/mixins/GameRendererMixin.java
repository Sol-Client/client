/*
 * Sol Client - an open source Minecraft client
 * Copyright (C) 2021-2023  TheKodeToad and Contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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
