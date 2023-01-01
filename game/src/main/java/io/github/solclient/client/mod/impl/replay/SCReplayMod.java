package io.github.solclient.client.mod.impl.replay;

import java.util.*;

import com.google.gson.annotations.Expose;
import com.replaymod.core.ReplayMod;
import com.replaymod.lib.de.johni0702.minecraft.gui.versions.callbacks.OpenGuiScreenCallback;

import io.github.solclient.client.Client;
import io.github.solclient.client.event.EventHandler;
import io.github.solclient.client.event.impl.*;
import io.github.solclient.client.mod.*;
import io.github.solclient.client.mod.annotation.*;
import io.github.solclient.client.mod.hud.*;
import io.github.solclient.client.mod.impl.replay.fix.SCReplayModBackend;
import io.github.solclient.client.ui.screen.mods.MoveHudsScreen;
import io.github.solclient.client.util.data.Colour;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.resources.I18n;

/**
 * Sol Client representation of Replay Mod. This allows it to appear in the mod
 * list.
 *
 * Originally by CrushedPixel and johni0702.
 */
public class SCReplayMod extends Mod {

	public static boolean enabled;
	public static Boolean deferedState;
	public static SCReplayMod instance;

	@Expose
	@Option
	public boolean enableNotifications = true;

	@Expose
	@Option
	public boolean recordSingleplayer = true;
	@Expose
	@Option
	public boolean recordServer = true;

	@Expose
	@Option
	public boolean recordingIndicator = true;
	@Expose
	@Option
	public Colour recordingIndicatorColour = Colour.BLACK;
	@Expose
	@Option
	@Slider(min = 50, max = 150, step = 1, format = "sol_client.slider.percent")
	protected float recordingIndicatorScale = 100;
	@Expose
	@Option
	protected Colour recordingIndicatorTextColour = Colour.WHITE;
	@Expose
	@Option
	protected boolean recordingIndicatorTextShadow = true;
	@Expose
	protected HudPosition recordingIndicatorPosition = new HudPosition(0.1F, 0.1F);
	private RecordingIndicator recordingIndicatorHud = new RecordingIndicator(this);

	@Expose
	@Option
	public boolean automaticRecording = true;

	@Expose
	@Option
	public boolean renameDialog = true;
	@Expose
	@Option
	public boolean showChat = true;

	@Expose
	@Option
	public SCCameraType camera = SCCameraType.CLASSIC;
	@Expose
	@Option
	public boolean showPathPreview = true;
	@Expose
	@Option
	public SCInterpolatorType defaultInterpolator = SCInterpolatorType.CATMULL;
	@Expose
	@Option
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

	private final List<Object> unregisterOnDisable = new ArrayList<>();
	private final List<Object> registerOnEnable = new ArrayList<>();

	private SCReplayModBackend backend;

	@Override
	public String getId() {
		return "replay";
	}

	@Override
	public String getCredit() {
		return I18n.format("sol_client.modified_from", "CrushedPixel, johni0702");
	}

	@Override
	public ModCategory getCategory() {
		return ModCategory.UTILITY;
	}

	@Override
	public void onRegister() {
		instance = this;

		backend = new SCReplayModBackend();
		backend.init();

		Client.INSTANCE.bus.register(new ConstantListener());

		super.onRegister();
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
			if (event.type == GameOverlayElement.ALL && enabled && !isEnabled()) {
				render(mc.currentScreen instanceof MoveHudsScreen);
			}
		}

	}

	private void updateState(WorldClient world) {
		updateSettings();

		if (world == null && deferedState != null && deferedState != enabled) {
			enabled = deferedState;
			if (deferedState) {
				for (Object event : registerOnEnable) {
					Client.INSTANCE.bus.register(event);
					unregisterOnDisable.add(event);
				}
				registerOnEnable.clear();

				OpenGuiScreenCallback.EVENT.invoker().openGuiScreen(mc.currentScreen);
			} else {
				for (Object event : unregisterOnDisable) {
					Client.INSTANCE.bus.unregister(event);
					registerOnEnable.add(event);
				}
				unregisterOnDisable.clear();
			}
			deferedState = null;
		}
	}

	public void addEvent(Object event) {
		if (isEnabled()) {
			unregisterOnDisable.add(event);
		} else {
			registerOnEnable.add(event);
			Client.INSTANCE.bus.unregister(event);
		}
	}

	public void removeEvent(Object event) {
		registerOnEnable.remove(event);
		unregisterOnDisable.remove(event);
	}

}
