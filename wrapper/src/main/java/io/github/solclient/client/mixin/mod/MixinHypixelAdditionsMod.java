package io.github.solclient.client.mixin.mod;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.solclient.client.mod.impl.hypixeladditions.HypixelAdditionsMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.*;
import net.minecraft.util.Formatting;

public class MixinHypixelAdditionsMod {

	@Mixin(PlayerEntityRenderer.class)
	public static abstract class MixinPlayerEntityRenderer extends EntityRenderer<AbstractClientPlayerEntity> {

		protected MixinPlayerEntityRenderer(EntityRenderDispatcher dispatcher) {
			super(dispatcher);
		}

		@Inject(method = "method_10209(Lnet/minecraft/client/network/AbstractClientPlayerEntity;DDDLjava/lang/String;FD)V", at = @At("RETURN"))
		public void renderLevelhead(AbstractClientPlayerEntity entityIn, double x, double y, double z, String str,
				float p_177069_9_, double p_177069_10_, CallbackInfo callback) {
			if (HypixelAdditionsMod.isEffective()) {
				String levelhead = HypixelAdditionsMod.instance.getLevelhead(
						entityIn == MinecraftClient.getInstance().player, entityIn.getName().asFormattedString(),
						entityIn.getUuid());

				if (levelhead != null)
					renderLabelIfPresent(entityIn, Formatting.AQUA + "Level: " + Formatting.YELLOW + levelhead, x,
							y + ((double) ((float) getFontRenderer().fontHeight * 1.15F * p_177069_9_)), z, 64);
			}
		}

	}

}
