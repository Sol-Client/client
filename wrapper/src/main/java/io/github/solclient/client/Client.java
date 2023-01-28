package io.github.solclient.client;

import java.io.*;
import java.nio.file.*;

import org.apache.logging.log4j.*;
import org.lwjgl.input.Keyboard;

import io.github.solclient.client.chatextensions.ChatExtensionManager;
import io.github.solclient.client.command.CommandManager;
import io.github.solclient.client.event.EventBus;
import io.github.solclient.client.mod.*;
import io.github.solclient.client.packet.*;
import io.github.solclient.client.ui.screen.mods.ModsScreen;
import io.github.solclient.client.util.*;
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
	private final PinManager pins = new PinManager();
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

	// for convenience with multimc
	@Getter
	private final Path configFolder = mc.runDirectory.toPath().resolve("config/sol-client"),
			modsFile = configFolder.resolve("mods.json"), pinsFile = configFolder.resolve("pins.json");

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
		try {
			pins.load(pinsFile);
		} catch (Throwable error) {
			LOGGER.error("Could not load pins", error);
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
			MinecraftUtils.ensureDirectory(configFolder);

			if (Files.exists(legacyModsFile) && !Files.exists(modsFile))
				Files.move(legacyModsFile, modsFile);
		} catch (Throwable error) {
			LOGGER.error("Failed to prepare load", error);
		}
	}

	public void save() {
		try {
			pins.save(pinsFile);
		} catch (IOException error) {
			LOGGER.error("Could not save pins", error);
		}

		try {
			mods.saveStandard(modsFile);
		} catch (IOException error) {
			LOGGER.error("Could not save mods", error);
		}
	}

	/**
	 * Saves if the mod screen is not opened.
	 */
	public void optionChanged() {
		if (!(mc.currentScreen instanceof ModsScreen))
			save();
	}

}
