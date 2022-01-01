package me.mcblueparrot.client.discord;

import java.io.File;
import java.io.IOException;
import java.time.Instant;

import com.replaymod.replay.ReplayModReplay;

import de.jcm.discordgamesdk.Core;
import de.jcm.discordgamesdk.CreateParams;
import de.jcm.discordgamesdk.activity.Activity;
import de.jcm.discordgamesdk.activity.ActivityType;
import me.mcblueparrot.client.event.EventHandler;
import me.mcblueparrot.client.event.impl.GameQuitEvent;
import me.mcblueparrot.client.event.impl.OpenGuiEvent;
import me.mcblueparrot.client.event.impl.PostTickEvent;
import me.mcblueparrot.client.event.impl.WorldLoadEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiMultiplayer;

public class DiscordRPC {

	private Minecraft mc = Minecraft.getMinecraft();
	private CreateParams params;
	private Core core;
	private Activity activity;
	private boolean state = true;

	public DiscordRPC() throws IOException {
		Core.init(new File(System.getProperty("me.mcblueparrot.client.discord_lib")));

		params = new CreateParams();
		params.setClientID(925701938211868683L);
		params.setFlags(CreateParams.Flags.toLong(CreateParams.Flags.NO_REQUIRE_DISCORD));

		core = new Core(params);
	}

	@EventHandler
	public void onTick(PostTickEvent event) {
		core.runCallbacks();
	}

	@EventHandler
	public void onGameQuit(GameQuitEvent event) {
		params.close();
		core.close();
	}

	@EventHandler
	public void onGuiChange(OpenGuiEvent event) {
		if((event.screen instanceof GuiMainMenu || event.screen instanceof GuiMultiplayer) && state && mc.theWorld == null) {
			noWorld();
		}
	}

	@EventHandler
	public void onWorldChange(WorldLoadEvent event) {
		if(!state && event.world != null) {
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
	}

	private void noWorld() {
		setActivity("Main Menu");
		state = false;
	}

	private void setActivity(String text) {
		if(activity != null) {
			activity.close();
		}

		activity = new Activity();
		activity.setDetails(text);
		activity.setType(ActivityType.PLAYING);
		activity.assets().setLargeImage("large_logo");
		activity.timestamps().setStart(Instant.now());

		core.activityManager().updateActivity(activity);

		state = true;
	}

}
