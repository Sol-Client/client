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

package io.github.solclient.client.mod.impl.replay.fix;

import java.lang.reflect.*;
import java.util.*;

import com.replaymod.core.events.SettingsChangedCallback;

import io.github.solclient.client.Client;
import io.github.solclient.client.mod.impl.replay.*;

/*
 * Includes modified decompiled Replay Mod class files (I didn't want to remove all the preprocessor comments :P).
 *
 * License for Replay Mod:
 *
 *     Copyright (C) <year>  <name of author>
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
public class SCSettingsRegistry {

	private final Map<SettingKey<?>, Object> settings = Collections.synchronizedMap(new LinkedHashMap<>());

	private SCReplayMod mod = SCReplayMod.instance;

	public void register() {
	}

	public void register(Class<?> settingsClass) {
		for (Field field : settingsClass.getDeclaredFields()) {
			if ((field.getModifiers() & (Modifier.STATIC | Modifier.PUBLIC)) != 0
					&& SettingKey.class.isAssignableFrom(field.getType())) {
				try {
					register((SettingKey<?>) field.get(null));
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void register(SettingKey<?> key) {
		settings.put(key, key.getDefault());
	}

	public Set<SettingKey<?>> getSettings() {
		return settings.keySet();
	}

	@SuppressWarnings("unchecked")
	public void update() {
		settings.forEach((key, value) -> set((SettingKey) key, get(key)));
	}

	@SuppressWarnings("unchecked")
	public <T> T get(SettingKey<T> key) {
		if (!settings.containsKey(key)) {
			throw new IllegalArgumentException("Setting " + key + " unknown.");
		}
		switch (key.getCategory()) {
			case "core":
				if (key.getKey().equals("notifications")) {
					return (T) (Boolean) mod.enableNotifications;
				}
				break;
			case "recording":
				switch (key.getKey()) {
					case "autoPostProcess":
						return (T) (Boolean) mod.automaticPostProcessing;
					case "autoStartRecording":
						return (T) (Boolean) mod.automaticRecording;
					case "indicator":
						return (T) (Boolean) mod.recordingIndicator;
					case "recordServer":
						return (T) (Boolean) (SCReplayMod.enabled && mod.recordServer);
					case "recordSingleplayer":
						return (T) (Boolean) (SCReplayMod.enabled && mod.recordSingleplayer);
					case "renameDialog":
						return (T) (Boolean) mod.renameDialog;
				}
				break;
			case "render":
				if (key.getKey().equals("frameTimeFromWorldTime")) {
					return (T) (Boolean) SCReplayMod.instance.frameTimeFromWorldTime;
				}
				break;
			case "replay":
				switch (key.getKey()) {
					case "camera":
						switch (mod.camera) {
							case CLASSIC:
								return (T) "replaymod.camera.classic";
							case VANILLA_ISH:
								return (T) "replaymod.camera.vanilla";
						}
						break;
					case "showChat":
						return (T) (Boolean) mod.showChat;
					case "showServerIPs":
						return (T) (Boolean) mod.showServerIPs;
					case "mainMenuButton":
						return (T) "DEFAULT";
				}
				break;
			case "simplepathing":
				switch (key.getKey()) {
					case "autosync":
						return (T) (Boolean) mod.autoSync;
					case "interpolator":
						switch (mod.defaultInterpolator) {
							case CATMULL:
								return (T) "replaymod.gui.editkeyframe.interpolator.catmullrom.name";
							case CUBIC:
								return (T) "replaymod.gui.editkeyframe.interpolator.cubic.name";
							case LINEAR:
								return (T) "replaymod.gui.editkeyframe.interpolator.linear.name";
						}
						break;
					case "pathpreview":
						return (T) (Boolean) mod.showPathPreview;
					case "timelineLength":
						return (T) (Integer) mod.timelineLength;
				}
			case "advanced":
				switch (key.getKey()) {
					case "askForOpenEye":
						return (T) Boolean.FALSE; // Sorry, ReplayMod devs, but you would receive crashes from Sol
													// Client.
					case "cachePath":
						return (T) "./.replay_cache/";
					case "fullBrightness":
						return (T) "replaymod.gui.settings.fullbrightness.gamma";
					case "recordingPath":
						return (T) "./replay_recordings/";
					case "renderPath":
						return (T) "./replay_videos/";
					case "skipPostRenderGui":
						return (T) (Boolean) mod.skipPostRenderGui;
					case "skipPostScreenshotGui":
						return (T) (Boolean) mod.skipPostScreenshotGui;
				}
		}
		return (T) settings.get(key);
	}

	public <T> void set(SettingKey<T> key, T value) {
		settings.put(key, value);
		switch (key.getCategory()) {
			case "core":
				if (key.getKey().equals("notifications")) {
					mod.enableNotifications = (Boolean) value;
				}
				break;
			case "recording":
				switch (key.getKey()) {
					case "autoPostProcess":
						mod.automaticPostProcessing = (Boolean) value;
						break;
					case "autoStartRecording":
						mod.automaticRecording = (Boolean) value;
						break;
					case "indicator":
						mod.recordingIndicator = (Boolean) value;
						break;
					case "recordServer":
						if (SCReplayMod.enabled)
							mod.recordServer = (Boolean) value;
						break;
					case "recordSingleplayer":
						if (SCReplayMod.enabled)
							mod.recordSingleplayer = (Boolean) value;
						break;
					case "renameDialog":
						mod.renameDialog = (Boolean) value;
						break;
				}
				break;
			case "replay":
				switch (key.getKey()) {
					case "camera":
						switch ((String) value) {
							case "replaymod.camera.classic":
								mod.camera = SCCameraType.CLASSIC;
								break;
							case "replaymod.camera.vanilla":
								mod.camera = SCCameraType.VANILLA_ISH;
								break;
						}
						break;
					case "showChat":
						mod.showChat = (Boolean) value;
						break;
					case "showServerIPs":
						mod.showServerIPs = (Boolean) value;
						break;
				}
				break;
			case "simplepathing":
				switch (key.getKey()) {
					case "autosync":
						mod.autoSync = (Boolean) value;
						break;
					case "interpolator":
						switch ((String) value) {
							case "replaymod.gui.editkeyframe.interpolator.catmullrom.name":
								mod.defaultInterpolator = SCInterpolatorType.CATMULL;
								break;
							case "replaymod.gui.editkeyframe.interpolator.cubic.name":
								mod.defaultInterpolator = SCInterpolatorType.CUBIC;
								break;
							case "replaymod.gui.editkeyframe.interpolator.linear.name":
								mod.defaultInterpolator = SCInterpolatorType.LINEAR;
								break;
						}
						break;
					case "pathpreview":
						mod.showPathPreview = (Boolean) value;
						break;
					case "timelineLength":
						mod.timelineLength = (Integer) value;
						break;
				}
				break;
			case "advanced":
				switch (key.getKey()) {
					case "skipPostRenderGui":
						mod.skipPostRenderGui = (Boolean) value;
						break;
					case "skipPostScreenshotGui":
						mod.skipPostScreenshotGui = (Boolean) value;
						break;
				}
		}
		SettingsChangedCallback.EVENT.invoker().onSettingsChanged(this, key);
	}

	public void save() {
		Client.INSTANCE.save();
	}

	public interface SettingKey<T> {

		String getCategory();

		String getKey();

		String getDisplayString();

		T getDefault();

	}

}
