package io.github.solclient.client.mixin.mod;

import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.solclient.client.mod.impl.cosmetica.CosmeticaMod;
import io.github.solclient.client.mod.impl.cosmetica.HatsLayer;
import io.github.solclient.client.mod.impl.cosmetica.ShoulderBuddies;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.RendererLivingEntity;

public class MixinCosmeticaMod {

	@Mixin(RenderPlayer.class)
	public static abstract class MixinRenderPlayer extends RendererLivingEntity<AbstractClientPlayer> {

		public MixinRenderPlayer(RenderManager rendermanagerIn, ModelBase modelbaseIn, float shadowsizeIn) {
			super(rendermanagerIn, modelbaseIn, shadowsizeIn);
		}

		@Inject(method = "<init>(Lnet/minecraft/client/renderer/entity/RenderManager;Z)V", at = @At("RETURN"))
		public void addLayers(CallbackInfo callback) {
			RenderPlayer th1s = (RenderPlayer) (Object) this;
			addLayer(new HatsLayer(th1s));
			addLayer(new ShoulderBuddies(th1s));
		}

		@Redirect(method = "renderOffsetLivingLabel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/RendererLivingEntity;renderOffsetLivingLabel(Lnet/minecraft/entity/Entity;DDDLjava/lang/String;FD)V"))
		public void renderLore(RendererLivingEntity<?> instance, net.minecraft.entity.Entity entityIn, double x,
				double y, double z, String str, float p_177069_9_, double p_177069_10_) {
			AbstractClientPlayer player = (AbstractClientPlayer) entityIn;
			if(CosmeticaMod.enabled) {
				Optional<String> lore = CosmeticaMod.instance.getLore(player);

				if(lore.isPresent()) {
					renderLivingLabel((AbstractClientPlayer) entityIn, lore.get(), x, y, z, 64);
					y += getFontRendererFromRenderManager().FONT_HEIGHT * 1.15F * p_177069_9_;
				}
			}

			super.renderOffsetLivingLabel((AbstractClientPlayer) entityIn, x, y, z, str, p_177069_9_, p_177069_10_);
		}

	}

}
