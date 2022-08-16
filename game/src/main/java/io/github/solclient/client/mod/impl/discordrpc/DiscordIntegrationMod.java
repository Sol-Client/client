package io.github.solclient.client.mod.impl.discordrpc;

import java.io.File;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import com.google.gson.annotations.Expose;

import de.jcm.discordgamesdk.Core;
import de.jcm.discordgamesdk.CreateParams;
import de.jcm.discordgamesdk.CreateParams.Flags;
import de.jcm.discordgamesdk.activity.Activity;
import de.jcm.discordgamesdk.activity.ActivityType;
import io.github.solclient.client.Constants;
import io.github.solclient.client.event.EventHandler;
import io.github.solclient.client.event.impl.game.PreTickEvent;
import io.github.solclient.client.event.impl.game.QuitEvent;
import io.github.solclient.client.event.impl.screen.ScreenSwitchEvent;
import io.github.solclient.client.event.impl.world.level.LevelLoadEvent;
import io.github.solclient.client.mod.Mod;
import io.github.solclient.client.mod.ModCategory;
import io.github.solclient.client.mod.annotation.Option;
import io.github.solclient.client.mod.annotation.Slider;
import io.github.solclient.client.mod.hud.HudElement;
import io.github.solclient.client.mod.hud.HudPosition;
import io.github.solclient.client.mod.hud.SimpleHudMod;
import io.github.solclient.client.mod.impl.discordrpc.socket.DiscordSocket;
import io.github.solclient.client.platform.mc.screen.MultiplayerScreen;
import io.github.solclient.client.platform.mc.screen.TitleScreen;
import io.github.solclient.client.platform.mc.world.level.ClientLevel;
import io.github.solclient.client.todo.TODO;
import io.github.solclient.client.ui.screen.SolClientMainMenu;
import io.github.solclient.client.util.Utils;
import io.github.solclient.client.util.data.Colour;
import io.github.solclient.client.util.data.VerticalAlignment;

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
			if(!Constants.DEV) {
				Core.init(new File(System.getProperty("io.github.solclient.client.discord_lib")));
			}
			else {
				Core.init(new File("./discord." + Utils.getNativeFileExtension()));
			}
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

			startActivity(mc.getLevel());
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
	public void onGameQuit(QuitEvent event) {
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
	public void onScreenSwitch(ScreenSwitchEvent event) {
		if(core == null) {
			return;
		}

		if ((event.getScreen() == null || event.getScreen() instanceof TitleScreen
				|| event.getScreen() instanceof SolClientMainMenu || event.getScreen() instanceof MultiplayerScreen)
				&& state && !mc.hasLevel()) {
			startActivity(null);
		}
	}

	@EventHandler
	public void onWorldChange(LevelLoadEvent event) {
		if(core == null) {
			return;
		}

		if(!state && event.getLevel() != null) {
			startActivity(event.getLevel());
		}
	}

	private void startActivity(ClientLevel level) {
		if(level != null) {
			if(mc.hasSingleplayerServer()) {
				setActivity("Singleplayer");
			}
			else {
				if(TODO.L /* TODO replaymod */ != null) {
					setActivity("Replay Viewer");
				}
				else {
					setActivity("Multiplayer - " + mc.getCurrentServer().getName());
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

		if(Constants.DEV) {
			activity.setDetails("Development Test");
		}

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
