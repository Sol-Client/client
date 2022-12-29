package io.github.solclient.client.mod.impl.discordrpc;

import java.io.File;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.replaymod.replay.ReplayModReplay;

import de.jcm.discordgamesdk.Core;
import de.jcm.discordgamesdk.CreateParams;
import de.jcm.discordgamesdk.CreateParams.Flags;
import de.jcm.discordgamesdk.activity.Activity;
import de.jcm.discordgamesdk.activity.ActivityType;
import io.github.solclient.client.Client;
import io.github.solclient.client.event.EventHandler;
import io.github.solclient.client.event.impl.GameQuitEvent;
import io.github.solclient.client.event.impl.OpenGuiEvent;
import io.github.solclient.client.event.impl.PreTickEvent;
import io.github.solclient.client.event.impl.WorldLoadEvent;
import io.github.solclient.client.mod.Mod;
import io.github.solclient.client.mod.ModCategory;
import io.github.solclient.client.mod.annotation.Option;
import io.github.solclient.client.mod.annotation.Slider;
import io.github.solclient.client.mod.hud.HudElement;
import io.github.solclient.client.mod.hud.HudPosition;
import io.github.solclient.client.mod.hud.SimpleHudMod;
import io.github.solclient.client.mod.impl.discordrpc.socket.DiscordSocket;
import io.github.solclient.client.ui.screen.SolClientMainMenu;
import io.github.solclient.client.util.Utils;
import io.github.solclient.client.util.data.Colour;
import io.github.solclient.client.util.data.VerticalAlignment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.multiplayer.WorldClient;

public class DiscordIntegrationMod extends Mod {

	public static DiscordIntegrationMod instance;

	private CreateParams params;
	private Core core;
	protected DiscordSocket socket;
	private Activity activity;
	private boolean state;

	@Expose
	@Option
	protected boolean voiceChatHud;
	@Expose
	@Option
	@Slider(min = 50, max = 150, step = 1, format = "sol_client.slider.percent")
	protected float voiceChatHudScale = 100;
	@Expose
	@Option
	protected VerticalAlignment voiceChatHudAlignment = VerticalAlignment.TOP;
	@Expose
	protected HudPosition voiceChatHudPosition = new HudPosition(0, 0);
	@Expose
	@Option
	protected Colour usernameColour = Colour.WHITE;
	@Expose
	@Option
	protected Colour mutedColour = new Colour(255, 80, 80);
	@Expose
	@Option(translationKey = SimpleHudMod.TRANSLATION_KEY)
	protected boolean shadow = true;
	@Expose
	@Option
	protected Colour speakingColour = new Colour(20, 255, 20);

	private DiscordVoiceChatHud discordVoiceChatHud;

	@Override
	public List<HudElement> getHudElements() {
		return Arrays.asList(discordVoiceChatHud);
	}

	@Override
	public void onRegister() {
		try {
			Core.init(new File(System.getProperty("io.github.solclient.client.discord_lib", "./discord." + Utils.getNativeFileExtension())));
		}
		catch(Exception error) {
			logger.error("Could not load natives", error);
		}

		instance = this;

		super.onRegister();
	}

	@Override
	public void postOptionChange(String key, Object value) {
		if(key.equals("voiceChatHud")) {
			closeSocket();
			if((boolean) value) {
				connectSocket();
			}
		}
	}

	@Override
	public void postStart() {
		super.postStart();
		discordVoiceChatHud = new DiscordVoiceChatHud(this);
	}

	@Override
	protected void onEnable() {
		super.onEnable();

		try {
			params = new CreateParams();
			params.setClientID(925701938211868683L);
			params.setFlags(Flags.NO_REQUIRE_DISCORD);
			core = new Core(params);

			startActivity(mc.theWorld);
		}
		catch(Throwable error) {
			logger.warn("Could not start GameSDK", error);
		}

		if(voiceChatHud) {
			connectSocket();
		}
	}

	private void connectSocket() {
		socket = new DiscordSocket(this);
		socket.connect();
	}

	private void closeSocket() {
		if(socket != null && !socket.isClosed()) {
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
		if(core == null) {
			return;
		}

		core.runCallbacks();
	}

	@EventHandler
	public void onGameQuit(GameQuitEvent event) {
		if(isEnabled() && core != null) {
			close();
		}
	}

	private void close() {
		if(core != null) {
			params.close();
			core.close();
			core = null;
		}

		closeSocket();
	}

	@EventHandler
	public void onGuiChange(OpenGuiEvent event) {
		if(core == null) {
			return;
		}

		if ((event.screen == null || event.screen instanceof GuiMainMenu || event.screen instanceof SolClientMainMenu
				|| event.screen instanceof GuiMultiplayer) && state && mc.theWorld == null) {
			startActivity(null);
		}
	}

	@EventHandler
	public void onWorldChange(WorldLoadEvent event) {
		if(core == null) {
			return;
		}

		if(!state && event.world != null) {
			startActivity(event.world);
		}
	}

	private void startActivity(WorldClient world) {
		if(world != null) {
			if(mc.isIntegratedServerRunning()) {
				setActivity("Singleplayer");
			}
			else {
				if(ReplayModReplay.instance.getReplayHandler() != null) {
					setActivity("Replay Viewer");
				}
				else {
					setActivity("Multiplayer - " + mc.getCurrentServerData().serverName);
				}
			}
		}
		else {
			setActivity("Main Menu");
			state = false;
		}
	}

	private void setActivity(String text) {
		if(activity != null) {
			activity.close();
		}

		activity = new Activity();
		activity.setState(text);

		activity.setType(ActivityType.PLAYING);
		activity.assets().setLargeImage("large_logo");
		activity.timestamps().setStart(Instant.now());

		core.activityManager().updateActivity(activity);

		state = true;
	}

	@Override
	public String getId() {
		return "discord_integration";
	}

	@Override
	public ModCategory getCategory() {
		return ModCategory.INTEGRATION;
	}

	public void socketError(Exception error) {
		logger.error("Discord socket error", error);

		closeSocket();
	}

}
