package io.github.solclient.client.mixin.mod;

import java.util.Optional;

import javax.swing.text.html.parser.Entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import io.github.solclient.client.mod.impl.cosmetica.CosmeticaMod;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.player.EntityPlayer;

public class MixinCosmeticaMod {

	@Mixin(RenderPlayer.class)
	public static abstract class MixinRenderPlayer extends Render<AbstractClientPlayer> {

		protected MixinRenderPlayer(RenderManager renderManager) {
			super(renderManager);
		}

		@Redirect(method = "renderOffsetLivingLabel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/RendererLivingEntity;renderOffsetLivingLabel(Lnet/minecraft/entity/Entity;DDDLjava/lang/String;FD)V"))
		public void renderLore(RendererLivingEntity<?> instance, net.minecraft.entity.Entity entityIn, double x, double y, double z, String str,
				float p_177069_9_, double p_177069_10_) {
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
