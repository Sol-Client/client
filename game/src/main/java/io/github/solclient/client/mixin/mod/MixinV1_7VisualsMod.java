package io.github.solclient.client.mixin.mod;

import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

import io.github.solclient.client.mod.impl.V1_7VisualsMod;
import io.github.solclient.client.util.access.AccessMinecraft;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.layers.LayerArmorBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public abstract class MixinV1_7VisualsMod {

	@Mixin(ItemRenderer.class)
	public static abstract class MixinItemRenderer {

		@Redirect(method = "renderItemInFirstPerson", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/ItemRenderer;transformFirstPersonItem(FF)V"))
		public void allowUseAndSwing(ItemRenderer itemRenderer, float equipProgress, float swingProgress) {
			transformFirstPersonItem(equipProgress,
					swingProgress == 0.0F && V1_7VisualsMod.enabled && V1_7VisualsMod.instance.useAndMine
							? mc.thePlayer
									.getSwingProgress(AccessMinecraft.getInstance().getTimerSC().renderPartialTicks)
							: swingProgress);
		}

		@Inject(method = "doBlockTransformations", at = @At("RETURN"))
		public void oldBlocking(CallbackInfo callback) {
			if (V1_7VisualsMod.enabled && V1_7VisualsMod.instance.blocking) {
				V1_7VisualsMod.oldBlocking();
			}
		}

		@Inject(method = "performDrinking", at = @At("HEAD"), cancellable = true)
		public void oldDrinking(AbstractClientPlayer clientPlayer, float partialTicks, CallbackInfo callback) {
			if (V1_7VisualsMod.enabled && V1_7VisualsMod.instance.eatingAndDrinking) {
				callback.cancel();
				V1_7VisualsMod.oldDrinking(itemToRender, clientPlayer, partialTicks);
			}
		}

		@Shadow
		protected abstract void transformFirstPersonItem(float equipProgress, float swingProgress);

		@Shadow
		private @Final Minecraft mc;

		@Shadow
		private ItemStack itemToRender;

	}

	@Mixin(EntityRenderer.class)
	public static abstract class MixinEntityRenderer {

		private float eyeHeightSubtractor;
		private long lastEyeHeightUpdate;

		@Redirect(method = "orientCamera", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;getEyeHeight()F"))
		public float smoothSneaking(Entity entity) {
			if (V1_7VisualsMod.enabled && V1_7VisualsMod.instance.sneaking && entity instanceof EntityPlayer) {
				EntityPlayer player = (EntityPlayer) entity;
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

		@Shadow
		private Minecraft mc;

	}

	@Mixin(LayerArmorBase.class)
	public static class MixinLayerArmorBase {

		@Inject(method = "shouldCombineTextures", at = @At("HEAD"), cancellable = true)
		public void oldArmour(CallbackInfoReturnable<Boolean> callback) {
			if (V1_7VisualsMod.enabled && V1_7VisualsMod.instance.armourDamage) {
				callback.setReturnValue(true);
			}
		}

	}

}
