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

package io.github.solclient.client.mod.impl.v1_7visuals.mixins;

import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.solclient.client.mod.impl.v1_7visuals.V1_7VisualsMod;
import io.github.solclient.client.util.MinecraftUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.item.ItemStack;

@Mixin(HeldItemRenderer.class)
public abstract class ItemRendererMixin {

	@Redirect(method = "renderArmHoldingItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/HeldItemRenderer;applyEquipAndSwingOffset(FF)V"))
	public void allowUseAndSwing(HeldItemRenderer instance, float equipProgress, float swingProgress) {
		applyEquipAndSwingOffset(equipProgress,
				swingProgress == 0.0F && V1_7VisualsMod.enabled && V1_7VisualsMod.instance.useAndMine
						? client.player.getHandSwingProgress(MinecraftUtils.getTickDelta())
						: swingProgress);
	}

	@Inject(method = "applySwordBlockTransformation", at = @At("RETURN"))
	public void oldBlocking(CallbackInfo callback) {
		if (V1_7VisualsMod.enabled && V1_7VisualsMod.instance.blocking) {
			V1_7VisualsMod.oldBlocking();
		}
	}

	@Inject(method = "applyEatOrDrinkTransformation", at = @At("HEAD"), cancellable = true)
	public void oldDrinking(AbstractClientPlayerEntity clientPlayer, float partialTicks, CallbackInfo callback) {
		if (V1_7VisualsMod.enabled && V1_7VisualsMod.instance.eatingAndDrinking) {
			callback.cancel();
			V1_7VisualsMod.oldDrinking(mainHand, clientPlayer, partialTicks);
		}
	}

	@Shadow
	protected abstract void applyEquipAndSwingOffset(float equipProgress, float swingProgress);

	@Shadow
	private @Final MinecraftClient client;

	@Shadow
	private ItemStack mainHand;

}
