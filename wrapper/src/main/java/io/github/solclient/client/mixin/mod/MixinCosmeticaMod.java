package io.github.solclient.client.mixin.mod;

import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.solclient.client.mod.impl.cosmetica.*;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.*;
import net.minecraft.client.render.entity.model.EntityModel;

public class MixinCosmeticaMod {

	@Mixin(PlayerEntityRenderer.class)
	public static abstract class MixinPlayerEntityRenderer extends LivingEntityRenderer<AbstractClientPlayerEntity> {

		public MixinPlayerEntityRenderer(EntityRenderDispatcher dispatcher, EntityModel model, float shadowSize) {
			super(dispatcher, model, shadowSize);
		}

		@Inject(method = "<init>(Lnet/minecraft/client/render/entity/EntityRenderDispatcher;Z)V", at = @At("RETURN"))
		public void addLayers(CallbackInfo callback) {
			PlayerEntityRenderer th1s = (PlayerEntityRenderer) (Object) this;
			addFeature(new HatsLayer(th1s));
			addFeature(new ShoulderBuddies(th1s));
		}

		@Redirect(method = "method_10209(Lnet/minecraft/client/network/AbstractClientPlayerEntity;DDDLjava/lang/String;FD)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/LivingEntityRenderer;method_10209(Lnet/minecraft/entity/Entity;DDDLjava/lang/String;FD)V"))
		public void renderLore(LivingEntityRenderer<?> instance, net.minecraft.entity.Entity entity, double x, double y,
				double z, String str, float p_177069_9_, double p_177069_10_) {
			AbstractClientPlayerEntity player = (AbstractClientPlayerEntity) entity;
			if (CosmeticaMod.enabled) {
				Optional<String> lore = CosmeticaMod.instance.getLore(player);

				if (lore.isPresent()) {
					renderLabelIfPresent((AbstractClientPlayerEntity) entity, lore.get(), x, y, z, 64);
					y += getFontRenderer().fontHeight * 1.15F * p_177069_9_;
				}
			}

			super.method_10209((AbstractClientPlayerEntity) entity, x, y, z, str, p_177069_9_, p_177069_10_);
		}

	}

}
