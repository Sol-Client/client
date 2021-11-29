package me.mcblueparrot.client.mod.impl;

import com.google.gson.annotations.Expose;
import com.replaymod.core.ReplayMod;
import com.replaymod.lib.de.johni0702.minecraft.gui.versions.callbacks.OpenGuiScreenCallback;
import lombok.AllArgsConstructor;
import me.mcblueparrot.client.Client;
import me.mcblueparrot.client.events.EventHandler;
import me.mcblueparrot.client.events.OpenGuiEvent;
import me.mcblueparrot.client.events.ServerConnectEvent;
import me.mcblueparrot.client.events.WorldLoadEvent;
import me.mcblueparrot.client.mod.Mod;
import me.mcblueparrot.client.mod.ModCategory;
import me.mcblueparrot.client.mod.annotation.ConfigOption;
import me.mcblueparrot.client.replaymod.SCReplayModBackend;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.WorldClient;

import java.util.ArrayList;
import java.util.List;

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
		super("Replay Mod", "replay", "Record, replay and share your gaming experience.", ModCategory.UTILITY);
		instance = this;

		backend = new SCReplayModBackend();
		backend.init();

		Client.INSTANCE.bus.register(new ServerListener());
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

	public class ServerListener {

		@EventHandler
		public void onWorldLoad(WorldLoadEvent event) {
			updateState(event.world);
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

	@Override
	public String getDescription() {
		if(deferedState != null && deferedState != enabled && SolClientMod.instance.fancyFont) {
			return super.getDescription() + " Log out to " + (deferedState ? "enable" : "disable") + ".";
		}
		return super.getDescription();
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

	@AllArgsConstructor
	public enum SCCameraType {
		CLASSIC("Classic"),
		VANILLA_ISH("Vanilla-ish");

		private String name;

		@Override
		public String toString() {
			return name;
		}
	}

	@AllArgsConstructor
	public enum SCInterpolatorType {
		CATMULL("Catmull-Rom Spline"),
		CUBIC("Cubic Spline"),
		LINEAR("Linear");

		private String name;

		@Override
		public String toString() {
			return name;
		}
	}

}
