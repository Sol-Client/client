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

package io.github.solclient.client.mod.impl;

import java.io.*;
import java.util.List;

import org.lwjgl.input.Keyboard;

import com.google.gson.*;
import com.google.gson.annotations.Expose;

import io.github.solclient.client.Client;
import io.github.solclient.client.mod.*;
import io.github.solclient.client.mod.option.*;
import io.github.solclient.client.mod.option.annotation.Option;
import io.github.solclient.client.mod.option.impl.ToggleOption;
import io.github.solclient.client.ui.Theme;
import io.github.solclient.client.ui.component.Component;
import io.github.solclient.util.*;
import net.minecraft.client.option.KeyBinding;

public class SolClientConfig extends ConfigOnlyMod {

	public static SolClientConfig instance;

	@Expose
	private boolean dark;
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

	@Override
	public String getId() {
		return "sol_client";
	}

	@Override
	public ModCategory getCategory() {
		return ModCategory.GENERAL;
	}

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
					Client.INSTANCE.getOnlinePlayers().logIn();
				} catch (IOException error) {
					logger.error("Could not log in", error);
				}
			} else {
				try {
					Client.INSTANCE.getOnlinePlayers().logOut();
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

}
