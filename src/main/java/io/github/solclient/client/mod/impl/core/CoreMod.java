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

package io.github.solclient.client.mod.impl.core;

import java.io.*;
import java.nio.file.Path;
import java.util.List;

import org.lwjgl.input.Keyboard;

import com.google.gson.*;
import com.google.gson.annotations.Expose;

import io.github.solclient.client.*;
import io.github.solclient.client.culling.CullTask;
import io.github.solclient.client.event.EventHandler;
import io.github.solclient.client.event.impl.*;
import io.github.solclient.client.mod.*;
import io.github.solclient.client.mod.impl.StandardMod;
import io.github.solclient.client.mod.impl.onlineindicators.OnlineIndicatorsMod;
import io.github.solclient.client.mod.option.*;
import io.github.solclient.client.mod.option.annotation.Option;
import io.github.solclient.client.mod.option.impl.ToggleOption;
import io.github.solclient.client.ui.Theme;
import io.github.solclient.client.ui.screen.mods.*;
import io.github.solclient.client.util.*;
import io.github.solclient.util.*;
import net.minecraft.client.option.*;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;

public class CoreMod extends StandardMod {

	public static CoreMod instance;

	@Expose
	private boolean dark = true;
	private int darkMut;
	@Expose
	@Option
	public boolean broadcastOnline = true;

	@Expose
	@Option
	public boolean onlineIndicator = true;

	@Expose
	@Option
	public boolean remindMeToUpdate = true;

	@Expose
	@Option
	public boolean fancyMainMenu = true;

	@Expose
	@Option
	public boolean logoInInventory;

	@Option
	public KeyBinding modsKey = new KeyBinding(getTranslationKey("mods"), Keyboard.KEY_RSHIFT,
			GlobalConstants.KEY_CATEGORY);

	@Option
	public KeyBinding editHudKey = new KeyBinding(getTranslationKey("edit_hud"), 0, GlobalConstants.KEY_CATEGORY);

	@Expose
	@Option
	public boolean openAnimation;

	@Expose
	@Option
	public boolean buttonClicks = true;

	@Expose
	@Option
	public boolean smoothScrolling = true;

	public SemVer latestRelease;
	private boolean remindedUpdate;
	private FilePollingTask pollingTask;

	@Override
	protected List<ModOption<?>> createOptions() {
		List<ModOption<?>> options = super.createOptions();
		options.add(0, new ToggleOption(getTranslationKey("option.dark"),
				ModOptionStorage.of(boolean.class, () -> dark, value -> {
					dark = value;
					darkMut++;
					setTheme();
				})) {

			@Override
			public String getName() {
				return darkMut >= 10 ? getTranslationKey("option.dark_easter_egg") : super.getName();
			}

		});
		return options;
	}

	@Override
	public void init() {
		super.init();

		instance = this;
		CpsMonitor.init();

		try {
			NanoVGManager.createContext();
		} catch (IOException error) {
			throw new UnsupportedOperationException("Cannot continue without nanovg", error);
		}

		try {
			pollingTask = new FilePollingTask(SolClient.INSTANCE);
		} catch (Throwable error) {
			logger.warn("Cannot create file polling task", error);
			pollingTask = null;
		}

		if (remindMeToUpdate) {
			Thread thread = new Thread(() -> {
				try (InputStream in = GlobalConstants.RELEASE_API.openStream()) {
					JsonObject object = new JsonParser().parse(new InputStreamReader(in)).getAsJsonObject();
					latestRelease = SemVer.parseOrNull(object.get("name").getAsString());
				} catch (Throwable error) {
					logger.warn("Could not check for updates", error);
				}
			});
			thread.setDaemon(true);
			thread.start();
		}

		setTheme();
	}

	@Override
	public boolean onOptionChange(String key, Object value) {
		if (key.equals("broadcastOnline")) {
			if ((Boolean) value) {
				try {
					OnlineIndicatorsMod.instance.logIn();
				} catch (IOException error) {
					logger.error("Could not log in", error);
				}
			} else {
				try {
					OnlineIndicatorsMod.instance.logOut();
				} catch (IOException error) {
					logger.error("Could not log out", error);
				}
			}
		}

		return true;
	}

	private void setTheme() {
		Theme.setCurrent(dark ? Theme.DARK : Theme.LIGHT);
	}

	@EventHandler
	public void onPostStart(PostGameStartEvent event) {
		SolClient.INSTANCE.forEach(Mod::lateInit);

		try {
			MinecraftUtils.unregisterKeyBinding((KeyBinding) GameOptions.class.getField("ofKeyBindZoom").get(mc.options));
		} catch (NoSuchFieldException | IllegalAccessException | ClassCastException ignored) {
			// OptiFine is not enabled.
		}

		new Thread(new CullTask()).start();
	}

	@EventHandler
	public void onWorldLoad(WorldLoadEvent event) {
		MinecraftUtils.USER_DATA.cancel();
		if (!remindedUpdate && CoreMod.instance.remindMeToUpdate) {
			remindedUpdate = true;
			SemVer latest = CoreMod.instance.latestRelease;
			if (latest != null && latest.isNewerThan(GlobalConstants.VERSION)) {
				Text message = new LiteralText("A new version of Sol Client is available: " + latest
						+ ".\nYou are currently on version " + GlobalConstants.VERSION_STRING + '.');
				message.setStyle(message.getStyle().setFormatting(Formatting.GREEN));
				mc.inGameHud.getChatHud().addMessage(message);
			}
		}
	}

	@EventHandler
	public void onTick(PreTickEvent event) {
		if (pollingTask != null)
			pollingTask.run();

		if (CoreMod.instance.modsKey.wasPressed())
			mc.setScreen(new ModsScreen());
		else if (CoreMod.instance.editHudKey.wasPressed()) {
			mc.setScreen(new ModsScreen());
			mc.setScreen(new MoveHudsScreen());
		}
	}

	@EventHandler
	public void onQuit(GameQuitEvent event) {
		NanoVGManager.closeContext();

		if (pollingTask != null)
			pollingTask.close();
	}

}
