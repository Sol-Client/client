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

package io.github.solclient.client.mod.impl.discord;

import java.io.File;
import java.time.Instant;
import java.util.*;

import com.google.gson.annotations.Expose;
import com.replaymod.replay.ReplayModReplay;

import de.jcm.discordgamesdk.*;
import de.jcm.discordgamesdk.CreateParams.Flags;
import de.jcm.discordgamesdk.activity.*;
import io.github.solclient.client.event.EventHandler;
import io.github.solclient.client.event.impl.*;
import io.github.solclient.client.mod.*;
import io.github.solclient.client.mod.hud.*;
import io.github.solclient.client.mod.impl.*;
import io.github.solclient.client.mod.impl.discord.socket.DiscordSocket;
import io.github.solclient.client.mod.option.annotation.*;
import io.github.solclient.client.ui.screen.SolClientMainMenu;
import io.github.solclient.client.util.MinecraftUtils;
import io.github.solclient.client.util.data.*;
import io.github.solclient.util.GlobalConstants;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.world.ClientWorld;

public class DiscordIntegrationMod extends SolClientMod {

	public static DiscordIntegrationMod instance;

	private CreateParams params;
	private Core core;
	protected DiscordSocket socket;
	private Activity activity;
	private boolean state;

	@Expose
	@Option
	private boolean ip;
	@Expose
	@Option
	boolean voiceChatHud;
	@Expose
	@Option
	@Slider(min = 50, max = 150, step = 1, format = "sol_client.slider.percent")
	float voiceChatHudScale = 100;
	@Expose
	@Option
	VerticalAlignment voiceChatHudAlignment = VerticalAlignment.TOP;
	@Expose
	Position voiceChatHudPosition;
	@Expose
	@Option
	Colour usernameColour = Colour.WHITE;
	@Expose
	@Option
	Colour mutedColour = new Colour(255, 80, 80);
	@Expose
	@Option
	Colour speakingColour = new Colour(20, 255, 20);
	@Expose
	@Option(translationKey = SolClientSimpleHudMod.TRANSLATION_KEY)
	boolean shadow = true;
	@Expose
	@Option
	@TextField("sol_client.mod.screen.default")
	private String applicationId = "";
	@Expose
	@Option
	@TextField("sol_client.mod.screen.default")
	private String icon = "";

	private final DiscordVoiceChatHud discordVoiceChatHud = new DiscordVoiceChatHud(this);

	@Override
	public String getId() {
		return "discord_integration";
	}

	@Override
	public ModCategory getCategory() {
		return ModCategory.INTEGRATION;
	}

	@Override
	public List<HudElement> getHudElements() {
		return Arrays.asList(discordVoiceChatHud);
	}

	@Override
	public void init() {
		try {
			Core.init(new File(System.getProperty("io.github.solclient.client.discord_lib",
					"./discord." + MinecraftUtils.getNativeFileExtension())));
		} catch (Exception error) {
			logger.error("Could not load natives", error);
		}

		instance = this;

		super.init();
	}

	@Override
	public void postOptionChange(String key, Object value) {
		if (key.equals("voiceChatHud")) {
			closeSocket();
			if ((boolean) value) {
				connectSocket();
			}
		}
	}

	@Override
	public void lateInit() {
		super.lateInit();
		discordVoiceChatHud.setFont(mc.textRenderer);
	}

	@Override
	protected void onEnable() {
		super.onEnable();

		try {
			params = new CreateParams();

			long id = GlobalConstants.DISCORD_APPLICATION;

			if (!applicationId.isEmpty()) {
				try {
					id = Long.parseLong(MinecraftUtils.onlyKeepDigits(applicationId));
				} catch (NumberFormatException ignored) {
				}
			}

			params.setClientID(id);
			params.setFlags(Flags.NO_REQUIRE_DISCORD);
			core = new Core(params);

			startActivity(mc.world);
		} catch (Throwable error) {
			logger.warn("Could not start GameSDK", error);
		}

		if (voiceChatHud) {
			connectSocket();
		}
	}

	private void connectSocket() {
		socket = new DiscordSocket(this);
		socket.connect();
	}

	private void closeSocket() {
		if (socket != null && !socket.isClosed()) {
			socket.close();
			socket = null;
		}
	}

	@Override
	protected void onDisable() {
		super.onDisable();
		close();
	}

	@EventHandler
	public void onTick(PreTickEvent event) {
		if (core == null) {
			return;
		}

		core.runCallbacks();
	}

	@EventHandler
	public void onGameQuit(GameQuitEvent event) {
		if (isEnabled() && core != null) {
			close();
		}
	}

	private void close() {
		if (core != null) {
			params.close();
			core.close();
			core = null;
		}

		closeSocket();
	}

	@EventHandler
	public void onGuiChange(OpenGuiEvent event) {
		if (core == null) {
			return;
		}

		if ((event.screen == null || event.screen instanceof TitleScreen || event.screen instanceof SolClientMainMenu
				|| event.screen instanceof MultiplayerScreen) && state && mc.world == null) {
			startActivity(null);
		}
	}

	@EventHandler
	public void onWorldChange(WorldLoadEvent event) {
		if (core == null) {
			return;
		}

		if (!state && event.world != null) {
			startActivity(event.world);
		}
	}

	private void startActivity(ClientWorld world) {
		if (world != null) {
			if (mc.isIntegratedServerRunning()) {
				setActivity(I18n.translate("menu.singleplayer"));
			} else {
				if (ReplayModReplay.instance.getReplayHandler() != null) {
					setActivity(I18n.translate("replaymod.gui.replayviewer"));
				} else {
					String server = mc.getCurrentServerEntry().name;
					if (ip)
						server += " (" + mc.getCurrentServerEntry().address + ')';

					setActivity(I18n.translate("sol_client.mod.discord_integration.multiplayer", server));
				}
			}
		} else {
			setActivity(I18n.translate("sol_client.mod.discord_integration.main_menu"));
			state = false;
		}
	}

	private void setActivity(String text) {
		if (activity != null) {
			activity.close();
		}

		activity = new Activity();
		activity.setState(text);

		activity.setType(ActivityType.PLAYING);
		activity.assets().setLargeImage(icon.isEmpty() ? "large_logo" : icon);
		activity.timestamps().setStart(Instant.now());

		core.activityManager().updateActivity(activity);

		state = true;
	}

	public void socketError(Exception error) {
		logger.error("Discord socket error", error);

		closeSocket();
	}

}
