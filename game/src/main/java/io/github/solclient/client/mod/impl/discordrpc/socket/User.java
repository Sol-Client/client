package io.github.solclient.client.mod.impl.discordrpc.socket;

import java.util.concurrent.CompletableFuture;

import org.lwjgl.opengl.GL11;

import com.google.gson.JsonObject;

import io.github.solclient.client.mod.impl.discordrpc.DiscordIntegrationMod;
import io.github.solclient.client.platform.mc.MinecraftClient;
import io.github.solclient.client.platform.mc.render.GlStateManager;
import io.github.solclient.client.platform.mc.resource.Identifier;
import io.github.solclient.client.platform.mc.texture.Texture;
import io.github.solclient.client.platform.mc.texture.TextureManager;
import io.github.solclient.client.util.data.Colour;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@EqualsAndHashCode
public class User {

	private static final String AVATAR_FORMAT = "https://cdn.discordapp.com/avatars/%s/%s.png?size=%d";

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
	private Texture avatarTexture;
	private CompletableFuture<Texture> expectedAvatarTexture;

	public void update(JsonObject data, JsonObject user) {
		name = data.get("nick").getAsString();

		if(user != null) {
			username = user.get("username").getAsString();
			discriminator = user.get("discriminator").getAsString();

			if(!user.get("avatar").isJsonNull()) {
				avatar = user.get("avatar").getAsString();
			}
		}

		if(data.has("voice_state")) {
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
		int scale = MinecraftClient.getInstance().getWindow().getScaleFactor();
		TextureManager texman = MinecraftClient.getInstance().getTextureManager();

		GlStateManager.resetColour();

		if(avatar == null) {
			texman.bind(Identifier.minecraft("textures/gui/discord_avatar_generic.png"));
			return;
		}

		if(!avatar.equals(boundAvatar) || boundScale != scale) {
			deleteTexture();

			expectedAvatarTexture = texman.download(String.format(AVATAR_FORMAT, id, avatar, (int) (16 * scale)));
			expectedAvatarTexture.thenAccept((texture) -> {
				expectedAvatarTexture = null;
				avatarTexture = texture;
			});

			boundAvatar = avatar;
			boundScale = scale;
		}
	}

	public void deleteTexture() {
		Texture texture = avatarTexture;
		CompletableFuture<Texture> expectedTexture = expectedAvatarTexture;

		MinecraftClient.getInstance().runSync(() -> {
			if(texture != null) {
				MinecraftClient.getInstance().getTextureManager().delete(texture);
			}
			else if(expectedTexture != null) {
				expectedTexture.cancel(false);
			}
		});
	}

}
