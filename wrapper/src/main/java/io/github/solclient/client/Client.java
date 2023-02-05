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

package io.github.solclient.client;

import java.io.*;
import java.nio.file.*;

import org.apache.logging.log4j.*;
import org.lwjgl.input.Keyboard;

import io.github.solclient.client.addon.AddonManager;
import io.github.solclient.client.chatextensions.ChatExtensionManager;
import io.github.solclient.client.command.CommandManager;
import io.github.solclient.client.event.EventBus;
import io.github.solclient.client.mod.*;
import io.github.solclient.client.packet.*;
import io.github.solclient.client.ui.screen.mods.ModsScreen;
import io.github.solclient.client.util.*;
import io.github.solclient.util.Utils;
import io.github.solclient.wrapper.ClassWrapper;
import lombok.Getter;
import net.minecraft.client.MinecraftClient;

/**
 * Main class for Sol Client.
 */
public final class Client {

	public static final Logger LOGGER = LogManager.getLogger();
	public static final Client INSTANCE = new Client();

	private final MinecraftClient mc = MinecraftClient.getInstance();

	// all of the managers etc.
	@Getter
	private final EventBus events = new EventBus();
	@Getter
	private final ModManager mods = new ModManager();
	@Getter
	private final ModUiStateManager modUiState = new ModUiStateManager();
	@Getter
	private final PacketApi packets = new PacketApi();
	@Getter
	private final PopupManager popups = new PopupManager();
	@Getter
	private final ChatExtensionManager chatExtensions = new ChatExtensionManager();
	@Getter
	private final CommandManager commands = new CommandManager();
	@Getter
	private final PseudoResourceManager pseudoResources = new PseudoResourceManager();
	@Getter
	private final AddonManager addons = AddonManager.getInstance();

	// for convenience with multimc
	@Getter
	private final Path configFolder = mc.runDirectory.toPath().resolve("config/sol-client"),
			modsFile = configFolder.resolve("mods.json"), modUiStateFile = configFolder.resolve("mod_ui_state.json");

	// used before 1.9.x
	private final Path legacyModsFile = mc.runDirectory.toPath().resolve("sol_client_mods.json");

	public void init() {
		// in this function, we try to use fairly heavy exception padding so nothing
		// escapes, but is logged
		// maybe it's not ideal, as suppressing some exceptions may cause others

		// misc stuff
		try {
			NanoVGManager.createContext();
		} catch (IOException error) {
			throw new IllegalStateException("Cannot initialise NanoVG", error);
		}

		MinecraftUtils.resetLineWidth();
		Keyboard.enableRepeatEvents(false);
		new File(mc.runDirectory, "server-resource-packs").mkdirs();

		CpsMonitor.forceInit();

		// load mods and data
		prepareLoad();
		mods.loadStandard(modsFile);
		addons.load(mods);
		try {
			modUiState.load(modUiStateFile);
		} catch (Throwable error) {
			LOGGER.error("Could not load mod ui state", error);
		}

		// register events
		events.register(this);
		events.register(new DefaultEvents());
		events.register(packets);
		events.register(popups);

		// save it all!
		save();
	}

	private void prepareLoad() {
		try {
			Utils.ensureDirectory(configFolder);

			if (Files.exists(legacyModsFile) && !Files.exists(modsFile))
				Files.move(legacyModsFile, modsFile);
		} catch (Throwable error) {
			LOGGER.error("Failed to prepare load", error);
		}
	}

	public void save() {
		try {
			modUiState.save(modUiStateFile);
		} catch (IOException error) {
			LOGGER.error("Could not save pins", error);
		}

		try {
			mods.saveStandard(modsFile);
		} catch (IOException error) {
			LOGGER.error("Could not save mods", error);
		}

		addons.save(mods);
	}

	/**
	 * Saves if the mod screen is not opened.
	 */
	public void optionChanged() {
		if (!(mc.currentScreen instanceof ModsScreen))
			save();
	}

	/**
	 * Gets the core class loader.
	 *
	 * @return the loader.
	 */
	public ClassLoader getClassLoader() {
		return ClassWrapper.getInstance();
	}

}
