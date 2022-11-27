package io.github.solclient.client.mod.impl.discordrpc.socket;

import com.google.gson.JsonObject;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;

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
	private ResourceLocation location;

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
		Minecraft mc = Minecraft.getMinecraft();
		TextureManager texman = mc.getTextureManager();
		int scale = new ScaledResolution(mc).getScaleFactor();

		GlStateManager.color(1, 1, 1);

		if(avatar == null) {
			texman.bindTexture(new ResourceLocation("textures/gui/discord_avatar_generic.png"));
			return;
		}

		if(!avatar.equals(boundAvatar) || boundScale != scale) {
			deleteTexture();

			location = new ResourceLocation("discord_avatar/" + avatar);
			texman.loadTexture(location, new ThreadDownloadImageData(null, String.format(AVATAR_FORMAT, id, avatar, (int) (16 * scale)), null, null));

			boundAvatar = avatar;
			boundScale = scale;
		}

		texman.bindTexture(location);
	}

	public void deleteTexture() {
		Minecraft mc = Minecraft.getMinecraft();

		mc.addScheduledTask(() -> {
			if(location != null) {
				Minecraft.getMinecraft().getTextureManager().deleteTexture(location);
			}
		});
	}

}
