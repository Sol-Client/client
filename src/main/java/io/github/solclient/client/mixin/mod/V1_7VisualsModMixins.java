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

package io.github.solclient.client.mixin.mod;

import com.mojang.blaze3d.platform.GlStateManager;
import io.github.solclient.client.mod.impl.V1_7VisualsMod;
import io.github.solclient.client.util.MinecraftUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.model.BiPedModel;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.UseAction;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public abstract class V1_7VisualsModMixins {

	@Mixin(ItemRenderer.class)
	public static abstract class ItemRendererMixin {

		@Unique
		private LivingEntity lastEntityToRenderFor = null;

		@Final
		private final MinecraftClient mc = MinecraftClient.getInstance();

		@Inject(method = "renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/client/render/model/json/ModelTransformation$Mode;)V", at = @At("HEAD"))
		public void renderItemModelForEntity(ItemStack stack, LivingEntity entity, ModelTransformation.Mode mode, CallbackInfo ci) {
			lastEntityToRenderFor = entity;
		}


		@Inject(method = "renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/BakedModel;Lnet/minecraft/client/render/model/json/ModelTransformation$Mode;)V", at = @At(value = "INVOKE",
				target = "Lnet/minecraft/client/render/item/ItemRenderer;renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/BakedModel;)V"))
		public void renderItemModelForEntity_renderItem(ItemStack stack, BakedModel model, ModelTransformation.Mode transformation, CallbackInfo ci) {
			if (V1_7VisualsMod.enabled && V1_7VisualsMod.instance.useAndMine &&
					transformation == ModelTransformation.Mode.THIRD_PERSON && lastEntityToRenderFor instanceof PlayerEntity &&
					mc.player.getStackInHand() != null && mc.player.getItemUseTicks() > 0 &&
					mc.player.getStackInHand().getUseAction() == UseAction.BLOCK) {
				GlStateManager.translate(-0.041, -0.095f, 0.212f);
				GlStateManager.rotate(80, 1.0f, 0.0f, 0.0f);
				GlStateManager.rotate(20, 0.0f, 1.0f, 0.0f);
				if (MinecraftClient.getInstance().player.isSneaking()) {
					GlStateManager.translate(-0.045, -0.0135f, 0.03);
				}
			}
		}

		@Mixin(HeldItemRenderer.class)
		public static abstract class HeldItemRendererMixin {

			@Shadow
			protected abstract void applyEquipAndSwingOffset(float equipProgress, float swingProgress);

			@Shadow
			private @Final MinecraftClient client;

			@Shadow
			private ItemStack mainHand;

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
					GlStateManager.scale(0.83F, 0.88F, 0.85F);
					GlStateManager.translate(-0.3F, 0.10F, -0.01F);
				}
			}

			@Inject(method = "applyEatOrDrinkTransformation", at = @At("HEAD"), cancellable = true)
			public void oldDrinking(AbstractClientPlayerEntity clientPlayer, float partialTicks, CallbackInfo callback) {
				if (V1_7VisualsMod.enabled && V1_7VisualsMod.instance.eatingAndDrinking) {
					callback.cancel();
					V1_7VisualsMod.oldDrinking(mainHand, clientPlayer, partialTicks);
				}
			}
		}

		@Mixin(GameRenderer.class)
		public static abstract class GameRendererMixin {

			private float eyeHeightSubtractor;
			private long lastEyeHeightUpdate;

			@Shadow
			private /* why you not final :( */ MinecraftClient client;

			// this code makes me long for spaghetti
			@Redirect(method = "transformCamera", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;getEyeHeight()F"))
			public float smoothSneaking(Entity entity) {
				if (V1_7VisualsMod.enabled && V1_7VisualsMod.instance.sneaking && entity instanceof PlayerEntity) {
					PlayerEntity player = (PlayerEntity) entity;
					float height = player.getEyeHeight();
					if (player.isSneaking()) {
						height += 0.08F;
					}
					float actualEyeHeightSubtractor = player.isSneaking() ? 0.08F : 0;
					long sinceLastUpdate = System.currentTimeMillis() - lastEyeHeightUpdate;
					lastEyeHeightUpdate = System.currentTimeMillis();
					if (actualEyeHeightSubtractor > eyeHeightSubtractor) {
						eyeHeightSubtractor += sinceLastUpdate / 500f;
						if (actualEyeHeightSubtractor < eyeHeightSubtractor) {
							eyeHeightSubtractor = actualEyeHeightSubtractor;
						}
					} else if (actualEyeHeightSubtractor < eyeHeightSubtractor) {
						eyeHeightSubtractor -= sinceLastUpdate / 500f;
						if (actualEyeHeightSubtractor > eyeHeightSubtractor) {
							eyeHeightSubtractor = actualEyeHeightSubtractor;
						}
					}
					return height - eyeHeightSubtractor;
				}
				return entity.getEyeHeight();
			}
		}

		@Mixin(ArmorFeatureRenderer.class)
		public static class LayerArmorBaseMixin {

			@Inject(method = "combineTextures", at = @At("HEAD"), cancellable = true)
			public void oldArmour(CallbackInfoReturnable<Boolean> callback) {
				if (V1_7VisualsMod.enabled && V1_7VisualsMod.instance.armourDamage)
					callback.setReturnValue(true);
			}
		}

		@Mixin(BiPedModel.class)
		public static class BiPedModelMixin {

			@ModifyConstant(method = "setAngles", constant = @Constant(floatValue = -0.5235988F))
			private float cancelRotation(float value) {
				return (V1_7VisualsMod.enabled && V1_7VisualsMod.instance.blocking) ? 0.0F : value;
			}
		}
	}
}
