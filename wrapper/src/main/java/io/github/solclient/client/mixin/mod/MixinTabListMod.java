package io.github.solclient.client.mixin.mod;

import java.util.UUID;

import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.solclient.client.mod.impl.hud.tablist.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.NetworkManager;
import net.minecraft.util.IChatComponent;

public class MixinTabListMod {

	@Mixin(GuiPlayerTabOverlay.class)
	public static class MixinGuiPlayerTabOverlay {

		@Redirect(method = "renderPlayerlist", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/FontRender"
				+ "er;drawStringWithShadow(Ljava/lang/String;FFI)I"))
		public int overrideShadow(FontRenderer instance, String text, float x, float y, int color) {
			if (TabListMod.enabled && !TabListMod.instance.textShadow) {
				return instance.drawString(text, x, y, color, false);
			}

			return instance.drawStringWithShadow(text, x, y, color);
		}

		@Redirect(method = "renderPlayerlist", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/"
				+ "WorldClient;getPlayerEntityByUUID(Ljava/util/UUID;)Lnet/minecraft/entity/player/EntityPlayer;"))
		public EntityPlayer overridePlayerHeads(WorldClient instance, UUID uuid) {
			if (TabListMod.enabled && TabListMod.instance.hidePlayerHeads) {
				return null;
			}

			return instance.getPlayerEntityByUUID(uuid);
		}

		@Redirect(method = "renderPlayerlist", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;"
				+ "isIntegratedServerRunning()Z"))
		public boolean overrideHead(Minecraft instance) {
			return instance.isIntegratedServerRunning() && shouldShowHeads();
		}

		@Redirect(method = "renderPlayerlist", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/"
				+ "NetworkManager;getIsencrypted()Z"))
		public boolean overrideHead(NetworkManager instance) {
			return instance.getIsencrypted() && shouldShowHeads();
		}

		private boolean shouldShowHeads() {
			return !(TabListMod.enabled && TabListMod.instance.hidePlayerHeads);
		}

		@ModifyConstant(method = "renderPlayerlist", constant = @Constant(intValue = Integer.MIN_VALUE))
		public int overrideBackground(int original) {
			if (TabListMod.enabled) {
				return TabListMod.instance.backgroundColour.getValue();
			}

			return original;
		}

		@ModifyConstant(method = "renderPlayerlist", constant = @Constant(intValue = 553648127))
		public int overrideEntryBackground(int original) {
			if (TabListMod.enabled) {
				return TabListMod.instance.entryBackgroundColour.getValue();
			}

			return original;
		}

		@Redirect(method = "renderPlayerlist", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/"
				+ "GuiPlayerTabOverlay;header:Lnet/minecraft/util/IChatComponent;"))
		public IChatComponent hideHeader(GuiPlayerTabOverlay instance) {
			if (TabListMod.enabled && TabListMod.instance.hideHeader) {
				return null;
			}

			return header;
		}

		@Redirect(method = "renderPlayerlist", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/"
				+ "GuiPlayerTabOverlay;footer:Lnet/minecraft/util/IChatComponent;"))
		public IChatComponent hideFooter(GuiPlayerTabOverlay instance) {
			if (TabListMod.enabled && TabListMod.instance.hideFooter) {
				return null;
			}

			return footer;
		}

		@Inject(method = "drawPing", at = @At("HEAD"), cancellable = true)
		public void drawNumeralPing(int p_175245_1_, int p_175245_2_, int p_175245_3_,
				NetworkPlayerInfo networkPlayerInfoIn, CallbackInfo callback) {
			if (TabListMod.enabled && TabListMod.instance.pingType != PingType.ICON) {
				callback.cancel();

				if (TabListMod.instance.pingType == PingType.NUMERAL) {
					int level;

					int ping = networkPlayerInfoIn.getResponseTime();

					if (ping < 0) {
						level = 5;
					} else if (ping < 150) {
						level = 0;
					} else if (ping < 300) {
						level = 1;
					} else if (ping < 600) {
						level = 2;
					} else if (ping < 1000) {
						level = 3;
					} else {
						level = 4;
					}

					String pingText = Integer.toString(ping);
					int colour = 0x00FF00;

					switch (level) {
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
					mc.fontRendererObj.drawString(pingText,
							(p_175245_2_ + p_175245_1_ - (mc.fontRendererObj.getStringWidth(pingText) * scale)) / scale
									- 2,
							p_175245_3_ / scale + 4, colour, TabListMod.instance.textShadow);

					GlStateManager.popMatrix();
				}
			}
		}

		@Shadow
		private IChatComponent header;

		@Shadow
		private IChatComponent footer;

		@Final
		@Shadow
		private Minecraft mc;

	}

}
