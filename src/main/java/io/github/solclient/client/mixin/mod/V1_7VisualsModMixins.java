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
import io.github.solclient.client.mixin.client.ClientPlayerInteractionManagerAccessor;
import io.github.solclient.client.mod.impl.v1_7visuals.*;
import io.github.solclient.client.util.MinecraftUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.hud.DebugHud;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.model.BiPedModel;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.Window;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BucketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
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
		public void renderItemModelForEntity(ItemStack stack, LivingEntity entity, ModelTransformation.Mode mode,
				CallbackInfo ci) {
			lastEntityToRenderFor = entity;
		}

		@Inject(method = "renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/BakedModel;Lnet/minecraft/client/render/model/json/ModelTransformation$Mode;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/ItemRenderer;renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/BakedModel;)V"))
		public void renderItemModelForEntity_renderItem(ItemStack stack, BakedModel model,
				ModelTransformation.Mode transformation, CallbackInfo ci) {
			if (V1_7VisualsMod.enabled && V1_7VisualsMod.instance.useAndMine
					&& transformation == ModelTransformation.Mode.THIRD_PERSON
					&& lastEntityToRenderFor instanceof PlayerEntity && mc.player.getStackInHand() != null
					&& mc.player.getItemUseTicks() > 0
					&& mc.player.getStackInHand().getUseAction() == UseAction.BLOCK) {
				GlStateManager.translate(-0.041, -0.095f, 0.212f);
				GlStateManager.rotate(80, 1.0f, 0.0f, 0.0f);
				GlStateManager.rotate(20, 0.0f, 1.0f, 0.0f);
				if (MinecraftClient.getInstance().player.isSneaking()) {
					GlStateManager.translate(-0.045, -0.0135f, 0.03);
				}
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

		private float lastEyeHeight;
		@Shadow
		private /* why you not final :( */ MinecraftClient client;

		// this code makes me long for spaghetti
		// edit: i've eaten it up, yum
		@Redirect(method = "transformCamera", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;getEyeHeight()F"))
		public float smoothSneaking(Entity entity, float tickDelta) {
			if (!(V1_7VisualsMod.enabled && V1_7VisualsMod.instance.sneaking))
				return entity.getEyeHeight();

			return lastEyeHeight + (((Sneaky) entity).get1_7InternalEyeHeight() - lastEyeHeight) * tickDelta;
		}

		@Redirect(method = "renderDebugCrosshair", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;getEyeHeight()F"))
		public float smoothDebugSneaking(Entity entity, float tickDelta) {
			return smoothSneaking(entity, tickDelta);
		}

		@Inject(method = "tick", at = @At("HEAD"))
		public void swap(CallbackInfo callback) {
			lastEyeHeight = ((Sneaky) client.getCameraEntity()).get1_7InternalEyeHeight();
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

	@Mixin(MinecraftClient.class)
	public static class MinecraftClientMixin {

		@Unique
		private boolean isHittingBlock = false;

		@Inject(method = "doUse", at = @At("HEAD"))
		public void rightClickMousePre(CallbackInfo ci) {
			if (V1_7VisualsMod.enabled && V1_7VisualsMod.instance.useAndMine
					&& MinecraftClient.getInstance().player.abilities.allowModifyWorld
					&& MinecraftClient.getInstance().player.getStackInHand() != null
					&& (MinecraftClient.getInstance().player.getStackInHand().getUseAction() != UseAction.NONE
							|| MinecraftClient.getInstance().player.getStackInHand().getItem() instanceof BucketItem
							|| MinecraftClient.getInstance().player.getStackInHand().getItem() instanceof BlockItem)) {
				isHittingBlock = MinecraftClient.getInstance().interactionManager.isBreakingBlock();
				MinecraftClient.getInstance().interactionManager.cancelBlockBreaking();
				((ClientPlayerInteractionManagerAccessor) MinecraftClient.getInstance().interactionManager)
						.setHitting(false);
			}
		}

		@Inject(method = "doUse", at = @At("RETURN"))
		public void rightClickMousePost(CallbackInfo ci) {
			if (V1_7VisualsMod.enabled && V1_7VisualsMod.instance.useAndMine
					&& !MinecraftClient.getInstance().interactionManager.isBreakingBlock()
					&& MinecraftClient.getInstance().player.abilities.allowModifyWorld
					&& MinecraftClient.getInstance().player.getStackInHand() != null
					&& (MinecraftClient.getInstance().player.getStackInHand().getUseAction() != UseAction.NONE
							|| MinecraftClient.getInstance().player.getStackInHand().getItem() instanceof BucketItem
							|| MinecraftClient.getInstance().player.getStackInHand().getItem() instanceof BlockItem)) {
				MinecraftClient.getInstance().interactionManager.cancelBlockBreaking();
				((ClientPlayerInteractionManagerAccessor) MinecraftClient.getInstance().interactionManager)
						.setHitting(isHittingBlock);
			}
		}
	}

	@Mixin(DebugHud.class)
	public static class DebugHudMixin {

		@Inject(method = "renderLeftText", at = @At("HEAD"), cancellable = true)
		public void render(CallbackInfo callback) {
			if (!active())
				return;

			callback.cancel();

			TextRenderer text = client.textRenderer;

			String debug = client.fpsDebugString;
			debug = debug.substring(0, client.fpsDebugString.indexOf(") ") + 1);
			debug = debug.replace(" (", ", ");
			debug = '(' + debug;
			// *really*
			debug = debug.replace("update)", "updates)");
			debug = "Minecraft 1.8.9 " + debug;
			text.drawWithShadow(debug, 2, 2, -1);

			String chunksDebug = client.worldRenderer.getChunksDebugString();
			text.drawWithShadow(chunksDebug, 2, 12, -1);

			text.drawWithShadow(client.worldRenderer.getEntitiesDebugString(), 2, 22, -1);
			text.drawWithShadow(String.format("P: %s T: %s", client.particleManager.getDebugString(),
					client.world.addDetailsToCrashReport()), 2, 32, -1);
			text.drawWithShadow(client.world.getDebugString(), 2, 42, -1);

			PlayerEntity player = client.player;
			World world = client.world;

			double x = player.x;
			double y = player.y;
			double z = player.z;

			int xFloor = MathHelper.floor(x);
			int yFloor = MathHelper.floor(y);
			int zFloor = MathHelper.floor(z);

			text.drawWithShadow(String.format("x: %.5f (%d) // c: %d (%d)", x, xFloor, xFloor >> 4, xFloor & 15), 2, 64,
					0xE0E0E0);
			text.drawWithShadow(String.format("y: %.3f (feet pos, %.3f eyes pos)", y,
					y + ((Sneaky) player).get1_7InternalEyeHeight()), 2, 72, 0xE0E0E0);
			text.drawWithShadow(String.format("z: %.5f (%d) // c: %d (%d)", z, zFloor, zFloor >> 4, zFloor & 15), 2, 80,
					0xE0E0E0);

			int f = MathHelper.floor(player.yaw * 4F / 360F + 0.5) & 3;
			text.drawWithShadow(
					String.format("f: %d (%s) / %.5f", f, DIRECTIONS[f], MathHelper.wrapDegrees(client.player.yaw)), 2,
					88, 0xE0E0E0);

			BlockPos absPos = new BlockPos(xFloor, yFloor, zFloor);
			if (world != null && world.blockExists(absPos)) {
				Chunk chunk = world.getChunk(absPos);
				BlockPos relativePos = new BlockPos(xFloor & 15, yFloor, zFloor & 15);
				text.drawWithShadow(
						String.format("lc: %d b: %s bl: %d sl %d rl: %d", chunk.getHighestNonEmptySectionYOffset() + 15,
								chunk.getBiomeAt(absPos, world.getBiomeSource()).name,
								chunk.getLightAtPos(LightType.BLOCK, relativePos),
								chunk.getLightAtPos(LightType.SKY, relativePos), chunk.getLightLevel(relativePos, 0)),
						2, 96, 0xE0E0E0);
			}

			text.drawWithShadow(String.format("ws: %.3f, fs: %.3f, g: %b, fl: %d", player.abilities.getWalkSpeed(),
					player.abilities.getFlySpeed(), player.onGround,
					MinecraftUtils.getHeightValue(world, xFloor, zFloor)), 2, 104, 0xE0E0E0);

			if (client.gameRenderer.areShadersSupported() && client.gameRenderer.getShader() != null)
				text.drawWithShadow(String.format("shader: %s", client.gameRenderer.getShader().getName()), 2, 112,
						0xE0E0E0);
		}

		@Inject(method = "renderRightText", at = @At("HEAD"), cancellable = true)
		public void render(Window window, CallbackInfo callback) {
			if (!active())
				return;

			callback.cancel();

			TextRenderer text = client.textRenderer;
			long max = Runtime.getRuntime().maxMemory();
			long total = Runtime.getRuntime().totalMemory();
			long free = Runtime.getRuntime().freeMemory();
			long used = total - free;

			String usedString = String.format("Used memory: %d%% (%dMB) of %dMB", used * 100 / max, used / 1024 / 1024,
					max / 1024 / 1024);
			text.drawWithShadow(usedString, window.getWidth() - text.getStringWidth(usedString), 2, 0xE0E0E0);

			String allocatedString = String.format("Allocated memory: %d%% (%dMB)", total * 100 / max,
					total / 1024 / 1024);
			text.drawWithShadow(allocatedString, window.getWidth() - text.getStringWidth(allocatedString), 12,
					0xE0E0E0);
		}

		@Shadow
		private @Final MinecraftClient client;

		private static final String[] DIRECTIONS = { "SOUTH", "WEST", "NORTH", "EAST" };

		private static boolean active() {
			return V1_7VisualsMod.enabled && V1_7VisualsMod.instance.debug;
		}

	}

	@Mixin(Entity.class)
	public static abstract class EntityMixin implements Sneaky {

		private float internalEyeHeight = Float.NaN;

		private boolean active() {
			return world.isClient && V1_7VisualsMod.enabled && V1_7VisualsMod.instance.sneaking && (Object) this == MinecraftClient.getInstance().getCameraEntity();
		}

		@Override
		public float get1_7InternalEyeHeight() {
			if (!active())
				return getEyeHeight();

			return internalEyeHeight;
		}

		@Inject(method = "tick", at = @At("HEAD"))
		public void tick(CallbackInfo callback) {
			if (!active())
				return;

			float realEyeHeight = getEyeHeight();
			// if we're aiming for a lower value, go straight there... for some reason
			if (realEyeHeight < internalEyeHeight || internalEyeHeight != internalEyeHeight)
				internalEyeHeight = getEyeHeight();
			else
				internalEyeHeight += (realEyeHeight - internalEyeHeight) * 0.6F;
		}

		@Shadow
		public abstract float getEyeHeight();

		@Shadow
		public World world;

	}

}
