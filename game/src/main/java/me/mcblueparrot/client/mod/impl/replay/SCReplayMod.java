package me.mcblueparrot.client.mod.impl.replay;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.replaymod.core.ReplayMod;
import com.replaymod.lib.de.johni0702.minecraft.gui.versions.callbacks.OpenGuiScreenCallback;

import me.mcblueparrot.client.Client;
import me.mcblueparrot.client.event.EventHandler;
import me.mcblueparrot.client.event.impl.GameOverlayElement;
import me.mcblueparrot.client.event.impl.PostGameOverlayRenderEvent;
import me.mcblueparrot.client.event.impl.WorldLoadEvent;
import me.mcblueparrot.client.mod.Mod;
import me.mcblueparrot.client.mod.ModCategory;
import me.mcblueparrot.client.mod.annotation.ConfigOption;
import me.mcblueparrot.client.mod.annotation.Slider;
import me.mcblueparrot.client.mod.hud.HudElement;
import me.mcblueparrot.client.mod.hud.HudPosition;
import me.mcblueparrot.client.mod.impl.SolClientMod;
import me.mcblueparrot.client.mod.impl.replay.fix.SCReplayModBackend;
import me.mcblueparrot.client.ui.screen.mods.MoveHudsScreen;
import me.mcblueparrot.client.util.data.Colour;
import net.minecraft.client.multiplayer.WorldClient;

/**
 * Sol Client representation of Replay Mod.
 * This allows it to appear in the mod list.
 *
 * Originally by CrushedPixel and johni0702.
 */
public class SCReplayMod extends Mod {

	public static boolean enabled;
	public static Boolean deferedState;
	public static SCReplayMod instance;

	@Expose
	@ConfigOption("Enable Notifications")
	public boolean enableNotifications = true;

	@Expose
	@ConfigOption("Record Singleplayer")
	public boolean recordSingleplayer = true;
	@Expose
	@ConfigOption("Record Server")
	public boolean recordServer = true;

	@Expose
	@ConfigOption("Recording Indicator")
	public boolean recordingIndicator = true;
	@Expose
	@ConfigOption("Recording Indicator Scale")
	@Slider(min = 50, max = 150, step = 1, suffix = "%")
	protected float recordingIndicatorScale = 100;
	@Expose
	@ConfigOption("Recording Indicator Text Colour")
	protected Colour recordingIndicatorTextColour = Colour.WHITE;
	@Expose
	@ConfigOption("Recording Indicator Text Shadow")
	protected boolean recordingIndicatorTextShadow = true;
	@Expose
	protected HudPosition recordingIndicatorPosition = new HudPosition(0.1F, 0.1F);
	private RecordingIndicator recordingIndicatorHud = new RecordingIndicator(this);

	@Expose
	@ConfigOption("Automatic Recording")
	public boolean automaticRecording = true;

	@Expose
	@ConfigOption("Rename Dialog")
	public boolean renameDialog = true;
	@Expose
	@ConfigOption("Show Chat")
	public boolean showChat = true;

	@Expose
	@ConfigOption("Camera")
	public SCCameraType camera = SCCameraType.CLASSIC;
	@Expose
	@ConfigOption("Show Path Preview")
	public boolean showPathPreview = true;
	@Expose
	@ConfigOption("Default Interpolator")
	public SCInterpolatorType defaultInterpolator = SCInterpolatorType.CATMULL;
	@Expose
	@ConfigOption("Show Server IPs")
	public boolean showServerIPs = true;

	@Expose
	public boolean automaticPostProcessing = true;
	@Expose
	public boolean autoSync = true;
	@Expose
	public int timelineLength = 1800;
	@Expose
	public boolean frameTimeFromWorldTime;

	@Expose
	public boolean skipPostRenderGui;
	public boolean skipPostScreenshotGui;

	private List<Object> unregisterOnDisable = new ArrayList<>();
	private List<Object> registerOnEnable = new ArrayList<>();

	private SCReplayModBackend backend;

	public SCReplayMod() {
		super("Replay", "replay", "Record and replay your gameplay. Modified from the Forge version: ReplayMod.", ModCategory.UTILITY);
		instance = this;

		backend = new SCReplayModBackend();
		backend.init();

		Client.INSTANCE.bus.register(new ConstantListener());
	}

	@Override
	public List<HudElement> getHudElements() {
		return Arrays.asList(recordingIndicatorHud);
	}

	private void updateSettings() {
		ReplayMod.instance.getSettingsRegistry().update();
	}

	@Override
	public boolean onOptionChange(String key, Object value) {
		return super.onOptionChange(key, value);
	}

	@Override
	public void postOptionChange(String key, Object value) {
		updateSettings();
	}

	@Override
	protected void onEnable() {
		super.onEnable();
		deferedState = Boolean.TRUE;

		updateState(mc.theWorld);
	}

	@Override
	protected void onDisable() {
		super.onDisable();
		deferedState = Boolean.FALSE;

		updateState(mc.theWorld);
	}

	public class ConstantListener {

		@EventHandler
		public void onWorldLoad(WorldLoadEvent event) {
			updateState(event.world);
		}

		@EventHandler
		public void onRender(PostGameOverlayRenderEvent event) {
			if(event.type == GameOverlayElement.ALL && enabled && !isEnabled()) {
				render(mc.currentScreen instanceof MoveHudsScreen);
			}
		}

	}

	private void updateState(WorldClient world) {
		updateSettings();

		if(world == null && deferedState != null && deferedState != enabled) {
			enabled = deferedState;
			if(deferedState) {
				for(Object event : registerOnEnable) {
					Client.INSTANCE.bus.register(event);
					unregisterOnDisable.add(event);
				}
				registerOnEnable.clear();

				OpenGuiScreenCallback.EVENT.invoker().openGuiScreen(mc.currentScreen);
			}
			else {
				for(Object event : unregisterOnDisable) {
					Client.INSTANCE.bus.unregister(event);
					registerOnEnable.add(event);
				}
				unregisterOnDisable.clear();
			}
			deferedState = null;
		}
	}

	public void addEvent(Object event) {
		if(isEnabled()) {
			unregisterOnDisable.add(event);
		}
		else {
			registerOnEnable.add(event);
			Client.INSTANCE.bus.unregister(event);
		}
	}

	public void removeEvent(Object event) {
		registerOnEnable.remove(event);
		unregisterOnDisable.remove(event);
	}

}
