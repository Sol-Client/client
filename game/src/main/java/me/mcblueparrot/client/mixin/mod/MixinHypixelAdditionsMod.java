package me.mcblueparrot.client.mixin.mod;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.mcblueparrot.client.mod.impl.hypixeladditions.HypixelAdditionsMod;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.util.EnumChatFormatting;

public class MixinHypixelAdditionsMod {

	@Mixin(RenderPlayer.class)
	public static abstract class MixinRenderPlayer extends Render<AbstractClientPlayer> {

		protected MixinRenderPlayer(RenderManager renderManager) {
			super(renderManager);
		}

		@Inject(method = "renderOffsetLivingLabel", at = @At("RETURN"))
		public void renderLevelhead(AbstractClientPlayer entityIn, double x, double y, double z, String str,
									float p_177069_9_, double p_177069_10_, CallbackInfo callback) {
			if(HypixelAdditionsMod.isEffective()) {
				String levelhead = HypixelAdditionsMod.instance.getLevelhead(entityIn.getUniqueID());
				if(levelhead != null) {
					renderLivingLabel(
							entityIn,
							EnumChatFormatting.AQUA + "Level: " + EnumChatFormatting.YELLOW + levelhead,
							x,
							y + ((double) ((float)this.getFontRendererFromRenderManager().FONT_HEIGHT
									* 1.15F * p_177069_9_)),
							z,
							64
					);
				}
			}
		}

	}

}
