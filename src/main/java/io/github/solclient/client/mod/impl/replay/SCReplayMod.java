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

package io.github.solclient.client.mod.impl.replay;

import java.util.*;

import com.google.gson.annotations.Expose;
import com.replaymod.core.ReplayMod;
import com.replaymod.lib.de.johni0702.minecraft.gui.versions.callbacks.OpenGuiScreenCallback;

import io.github.solclient.client.event.*;
import io.github.solclient.client.event.impl.*;
import io.github.solclient.client.mod.hud.*;
import io.github.solclient.client.mod.impl.StandardMod;
import io.github.solclient.client.mod.impl.replay.fix.SCReplayModBackend;
import io.github.solclient.client.mod.option.annotation.*;
import io.github.solclient.client.ui.screen.mods.MoveHudsScreen;
import io.github.solclient.client.util.data.*;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.world.ClientWorld;

/**
 * Sol Client representation of Replay Mod. This allows it to appear in the mod
 * list.
 *
 * Originally by CrushedPixel and johni0702.
 */
public class SCReplayMod extends StandardMod {

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
	protected Position recordingIndicatorPosition;
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
	public String getDetail() {
		return I18n.translate("sol_client.mod.screen.modified_from", "CrushedPixel, johni0702");
	}

	@Override
	public void init() {
		instance = this;

		backend = new SCReplayModBackend();
		backend.init();

		EventBus.INSTANCE.register(new ConstantListener());

		super.init();
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

		updateState(mc.world);
	}

	@Override
	protected void onDisable() {
		super.onDisable();
		deferedState = Boolean.FALSE;

		updateState(mc.world);
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

	private void updateState(ClientWorld world) {
		updateSettings();

		if (world == null && deferedState != null && deferedState != enabled) {
			enabled = deferedState;
			if (deferedState) {
				for (Object event : registerOnEnable) {
					EventBus.INSTANCE.register(event);
					unregisterOnDisable.add(event);
				}
				registerOnEnable.clear();

				OpenGuiScreenCallback.EVENT.invoker().openGuiScreen(mc.currentScreen);
			} else {
				for (Object event : unregisterOnDisable) {
					EventBus.INSTANCE.unregister(event);
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
			EventBus.INSTANCE.unregister(event);
		}
	}

	public void removeEvent(Object event) {
		registerOnEnable.remove(event);
		unregisterOnDisable.remove(event);
	}

}
