package io.github.solclient.client.mod.impl.hud.tablist.mixins;

import java.util.UUID;

import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.platform.GlStateManager;

import io.github.solclient.client.mod.impl.hud.tablist.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.ClientConnection;
import net.minecraft.text.Text;

@Mixin(PlayerListHud.class)
public class PlayerListHudMixin {

	// TODO: PLEASE FIX THIS TOAD!!
//		@Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;drawWithShadow(Ljava/lang/String;FFI)I"))
//		public int overrideShadow(TextRenderer instance, String text, float x, float y, int color) {
//			if (TabListMod.enabled && !TabListMod.instance.textShadow) {
//				return instance.draw(text, x, y, color, false);
//			}
//
//			return instance.drawWithShadow(text, x, y, color);
//		}

	@Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld;getPlayerByUuid(Ljava/util/UUID;)Lnet/minecraft/entity/player/PlayerEntity;"))
	public PlayerEntity overridePlayerHeads(ClientWorld instance, UUID uuid) {
		if (TabListMod.enabled && TabListMod.instance.hidePlayerHeads)
			return null;

		return instance.getPlayerByUuid(uuid);
	}

	@Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;isIntegratedServerRunning()Z"))
	public boolean overrideHead(MinecraftClient instance) {
		return instance.isIntegratedServerRunning() && shouldShowHeads();
	}

	@Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/ClientConnection;isEncrypted()Z"))
	public boolean overrideHead(ClientConnection instance) {
		return instance.isEncrypted() && shouldShowHeads();
	}

	private boolean shouldShowHeads() {
		return !(TabListMod.enabled && TabListMod.instance.hidePlayerHeads);
	}

	@ModifyConstant(method = "render", constant = @Constant(intValue = Integer.MIN_VALUE))
	public int overrideBackground(int original) {
		if (TabListMod.enabled)
			return TabListMod.instance.backgroundColour.getValue();

		return original;
	}

	@ModifyConstant(method = "render", constant = @Constant(intValue = 553648127))
	public int overrideEntryBackground(int original) {
		if (TabListMod.enabled) {
			return TabListMod.instance.entryBackgroundColour.getValue();
		}

		return original;
	}

	@Redirect(method = "render", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/hud/PlayerListHud;header:Lnet/minecraft/text/Text;"))
	public Text hideHeader(PlayerListHud instance) {
		if (TabListMod.enabled && TabListMod.instance.hideHeader)
			return null;

		return header;
	}

	@Redirect(method = "render", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/hud/PlayerListHud;footer:Lnet/minecraft/text/Text;"))
	public Text hideFooter(PlayerListHud instance) {
		if (TabListMod.enabled && TabListMod.instance.hideFooter)
			return null;

		return footer;
	}

	@Inject(method = "renderLatencyIcon", at = @At("HEAD"), cancellable = true)
	public void drawNumeralPing(int p_175245_1_, int p_175245_2_, int p_175245_3_, PlayerListEntry entry,
			CallbackInfo callback) {
		if (TabListMod.enabled && TabListMod.instance.pingType != PingType.ICON) {
			callback.cancel();

			if (TabListMod.instance.pingType == PingType.NUMERAL) {
				int level;

				int ping = entry.getLatency();

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
				client.textRenderer.draw(pingText,
						(p_175245_2_ + p_175245_1_ - (client.textRenderer.getStringWidth(pingText) * scale)) / scale
								- 2,
						p_175245_3_ / scale + 4, colour, TabListMod.instance.textShadow);

				GlStateManager.popMatrix();
			}
		}
	}

	@Shadow
	private Text header;

	@Shadow
	private Text footer;

	@Shadow
	private @Final MinecraftClient client;

}
