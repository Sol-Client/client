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

package io.github.solclient.client.mod.impl.discord.socket;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.platform.GlStateManager;

import lombok.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.*;
import net.minecraft.client.util.Window;
import net.minecraft.util.Identifier;

@RequiredArgsConstructor
@EqualsAndHashCode
public class User {

	private static String AVATAR_FORMAT = "https://cdn.discordapp.com/avatars/%s/%s.png?size=%d";

	@Getter
	private final String id;
	@Getter
	@Setter
	private String name;
	@Getter
	private String username;
	@Getter
	private String discriminator;
	@Getter
	private String avatar;
	private String boundAvatar;
	private float boundScale = -1;
	private boolean mute;
	private boolean selfMute;
	private boolean deafen;
	private boolean selfDeafen;
	@Getter
	@Setter
	private boolean speaking;
	private Identifier location;

	public void update(JsonObject data, JsonObject user) {
		name = data.get("nick").getAsString();

		if (user != null) {
			username = user.get("username").getAsString();
			discriminator = user.get("discriminator").getAsString();

			if (!user.get("avatar").isJsonNull()) {
				avatar = user.get("avatar").getAsString();
			}
		}

		if (data.has("voice_state")) {
			JsonObject voiceState = data.get("voice_state").getAsJsonObject();
			mute = voiceState.get("mute").getAsBoolean();
			selfMute = voiceState.get("self_mute").getAsBoolean();
			deafen = voiceState.get("deaf").getAsBoolean();
			selfDeafen = voiceState.get("self_deaf").getAsBoolean();
		}
	}

	public boolean isMuted() {
		return mute || selfMute || isDeaf();
	}

	public boolean isDeaf() {
		return deafen || selfDeafen;
	}

	public void bindTexture() {
		MinecraftClient mc = MinecraftClient.getInstance();
		TextureManager textures = mc.getTextureManager();
		int scale = new Window(mc).getScaleFactor();

		GlStateManager.color(1, 1, 1);

		if (avatar == null) {
			textures.bindTexture(new Identifier("sol_client", "textures/gui/discord_avatar_generic.png"));
			return;
		}

		if (!avatar.equals(boundAvatar) || boundScale != scale) {
			deleteTexture();

			location = new Identifier("discord_avatar/" + avatar);
			textures.loadTexture(location, new PlayerSkinTexture(null,
					String.format(AVATAR_FORMAT, id, avatar, 16 * scale), null, null));

			boundAvatar = avatar;
			boundScale = scale;
		}

		textures.bindTexture(location);
	}

	public void deleteTexture() {
		MinecraftClient mc = MinecraftClient.getInstance();

		mc.submit(() -> {
			if (location != null)
				MinecraftClient.getInstance().getTextureManager().close(location);
		});
	}

}
