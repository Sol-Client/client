package me.mcblueparrot.client.mixin.mod;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.mcblueparrot.client.mod.impl.NumeralPingMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;

public class MixinNumeralPingMod {


	@Mixin(GuiPlayerTabOverlay.class)
	public static class MixinGuiPlayerTabOverlay {

		@Shadow @Final
		private Minecraft mc;

		@Inject(method = "drawPing", at = @At("HEAD"), cancellable = true)
		public void drawNumeralPing(int p_175245_1_, int p_175245_2_, int p_175245_3_,
									NetworkPlayerInfo networkPlayerInfoIn, CallbackInfo callback) {
			if(NumeralPingMod.enabled) {
				callback.cancel();

				int level = 0;

				int ping = networkPlayerInfoIn.getResponseTime();

				if(ping < 0) {
					level = 5;
				}
				else if(ping < 150) {
					level = 0;
				}
				else if(ping < 300) {
					level = 1;
				}
				else if(ping < 600) {
					level = 2;
				}
				else if(ping < 1000) {
					level = 3;
				}
				else {
					level = 4;
				}

				String pingText = Integer.toString(ping);
				int colour = 0x00FF00;

				switch(level) {
					case 1:
						colour = 0xFFFF00;
						break;
					case 2:
						colour = 0xFF9600;
						break;
					case 3:
						colour = 0xFF6400;
						break;
					case 4:
					case 5:
						colour = 0xFF0000;
						break;
				}

				float scale = 0.5F;

				GlStateManager.pushMatrix();
				GlStateManager.scale(scale, scale, scale);
				mc.fontRendererObj.drawStringWithShadow(pingText,
						(p_175245_2_ + p_175245_1_ - (mc.fontRendererObj.getStringWidth(pingText) * scale)) / scale - 2, p_175245_3_ / scale + 4, colour);

				GlStateManager.popMatrix();
			}
		}

	}

}
