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

package io.github.solclient.client.mixin.client;

import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.platform.GlStateManager;

import io.github.solclient.client.Client;
import io.github.solclient.client.mod.impl.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.scoreboard.*;
import net.minecraft.util.Identifier;

@Mixin(PlayerListHud.class)
public abstract class PlayerListHudMixin {

	@Inject(method = "render", at = @At("HEAD"))
	public void preBatch(int width, Scoreboard scoreboard, ScoreboardObjective playerListScoreboardObjective,
			CallbackInfo callback) {
		if (SolClientConfig.instance.onlineIndicator)
			Client.INSTANCE.getOnlinePlayers().fetch(client.player.networkHandler.getPlayerList().stream()
					.map(PlayerListEntry::getProfile).map(GameProfile::getId));
	}

	@Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/PlayerListHud;getPlayerName(Lnet/minecraft/client/network/PlayerListEntry;)Ljava/lang/String;", ordinal = 1))
	public String captureEntry(PlayerListHud instance, PlayerListEntry entry) {
		sc$entry = entry;
		return instance.getPlayerName(entry);
	}

	@Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;drawWithShadow(Ljava/lang/String;FFI)I", ordinal = 2))
	public int renderIcon(TextRenderer instance, String string, float x, float y, int colour) {
		int end = instance.drawWithShadow(string, x, y, colour);

		if (SolClientConfig.instance.onlineIndicator
				&& Client.INSTANCE.getOnlinePlayers().recall(sc$entry.getProfile().getId())) {
			GlStateManager.color(1, 1, 1);
			client.getTextureManager().bindTexture(new Identifier("sol_client", "textures/gui/icon.png"));
			DrawableHelper.drawTexture(end, (int) y, 0, 0, 8, 8, 8, 8, 8, 8);
		}

		return end;
	}

	@Inject(method = "render", at = @At("RETURN"))
	public void clearEntry(int width, Scoreboard scoreboard, ScoreboardObjective playerListScoreboardObjective,
			CallbackInfo callback) {
		sc$entry = null;
	}

	@Shadow
	private @Final MinecraftClient client;
	private PlayerListEntry sc$entry;

}
