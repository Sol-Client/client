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
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Stream;

import org.apache.logging.log4j.*;
import org.spongepowered.asm.mixin.Mixins;

import com.google.gson.*;

import io.github.solclient.client.mod.*;
import io.github.solclient.client.mod.hud.HudElement;
import io.github.solclient.client.mod.impl.StandardMod;
import io.github.solclient.wrapper.*;
import lombok.Getter;
import net.minecraft.client.MinecraftClient;

public final class SolClient implements Iterable<Mod> {

	public static final SolClient INSTANCE = new SolClient();

	public static final Logger LOGGER = LogManager.getLogger();
	private static final Gson DEFAULT_GSON = getGson(null);

	private final List<Mod> mods = new ArrayList<>();
	private final Map<String, Mod> byId = new HashMap<>();
	private final List<HudElement> huds = new ArrayList<>();
	private List<ModInfo> queue = new ArrayList<>();
	@Getter
	private Path configFolder, modsFile;

	public void loadStandard() throws IOException, InvalidModException {
		LOGGER.info("Loading standard mods...");
		try (InputStream in = ClassWrapper.getInstance().getResourceAsStream("standard-mods.json")) {
			JsonArray array = new JsonParser().parse(new InputStreamReader(in, StandardCharsets.UTF_8)).getAsJsonArray();
			for (JsonElement element : array) {
				JsonObject object = element.getAsJsonObject();
				prepare(ModInfo.parse(null, "io.github.solclient.client.mod.impl", object));
			}
		}
	}

	public void saveAll() {
		try {
			saveStandard();
		} catch (IOException error) {
			LOGGER.error("Failed to save standard mods", error);
		}
	}

	public void saveStandard() throws IOException {
		JsonObject result = new JsonObject();

		for (Mod mod : mods)
			if (mod instanceof StandardMod)
				result.add(mod.getId(), save(mod));

		try (Writer out = new OutputStreamWriter(Files.newOutputStream(modsFile))) {
			out.write(result.toString());
		}
	}

	public void init() {
		MinecraftClient mc = MinecraftClient.getInstance();
		configFolder = mc.runDirectory.toPath().resolve("config/sol-client");
		modsFile = configFolder.resolve("mods.json");

		LOGGER.info("Loading {} mods...", queue.size());

		JsonObject storage = null;
		if (Files.isRegularFile(modsFile)) {
			try (Reader reader = new InputStreamReader(Files.newInputStream(modsFile), StandardCharsets.UTF_8)) {
				storage = new JsonParser().parse(reader).getAsJsonObject();
			} catch (Throwable error) {
				LOGGER.error("Could not load Sol Client mods storage", error);
			}
		}

		for (ModInfo info : queue) {
			try {
				Mod mod = info.construct();

				JsonObject storageNode = null;
				if (mod instanceof StandardMod && storage != null) {
					JsonElement storageElement = storage.get(mod.getId());
					if (storageElement != null) {
						if (!storageElement.isJsonObject())
							LOGGER.warn("Storage node for {} is not a JsonObject - its type is {}", mod.getId(),
									storageElement.getClass());
						else
							storageNode = storageElement.getAsJsonObject();
					}
				}

				register(mod, storageNode);
			} catch (ReflectiveOperationException error) {
				LOGGER.error("Could not construct mod {}", info.getId(), error);
			}
		}

		queue.clear();
		queue = null;
	}

	/**
	 * Registers a mod. This will make it appear in the menu.
	 *
	 * @param mod the mod to register.
	 */
	public void register(Mod mod, JsonObject config) {
		try {
			// quite broken
			if (Boolean.getBoolean("io.github.solclient.client.mod." + mod.getId() + ".disable"))
				return;

			if (mod instanceof StandardMod)
				((StandardMod) mod).setIndex(mods.size());

			try {
				configure(mod, config);
			} catch (Throwable error) {
				LOGGER.error("Could not configure mod {}" + mod.getId(), error);
			}

			mod.init();
			mods.add(mod);
			byId.put(mod.getId(), mod);
			huds.addAll(mod.getHudElements());
		} catch (Throwable error) {
			LOGGER.error("Could not register mod {}", mod.getId(), error);

			if (mod instanceof StandardMod)
				((StandardMod) mod).setIndex(-1);
		}
	}

	/**
	 * Loads a JSON configuration into a mod.
	 *
	 * @param mod    the mod.
	 * @param config the configuration object.
	 */
	public void configure(Mod mod, JsonObject config) {
		if (config == null)
			return;

        mod.loadConfig(config);
	}

	/**
	 * Dumps the config of a mod.
	 *
	 * @param mod the mod.
	 * @return the config as a json object.
	 */
	public JsonObject save(Mod mod) {
		return DEFAULT_GSON.toJsonTree(mod).getAsJsonObject();
	}

	/**
	 * Gets a mod - which may or may not be present - by its id.
	 *
	 * @param id the mod id.
	 * @return an optional mod.
	 */
	public Optional<Mod> getMod(String id) {
		return Optional.ofNullable(byId.get(id));
	}

	/**
	 * Gets a mod, or throws if it's not present.
	 *
	 * @param id the mod id.
	 * @return the mod - not <code>null</code>.
	 */
	public Mod getModOrThrow(String id) {
		Mod result = byId.get(id);
		if (result == null)
			throw new IllegalArgumentException(id);

		return result;
	}

	/**
	 * Gets the list of mods. Note: for efficiency, this returns the internal one.
	 *
	 * @return the list.
	 */
	public List<Mod> getMods() {
		return mods;
	}

	/**
	 * Gets the list of mods' huds. Note: for efficiency, this returns the internal
	 * one.
	 *
	 * @return the list.
	 */
	public List<HudElement> getHuds() {
		return huds;
	}

	@Override
	public Iterator<Mod> iterator() {
		return mods.iterator();
	}

	public Stream<Mod> modStream() {
		return mods.stream();
	}

	public List<ModInfo> getQueue() {
		if (queue == null)
			return Collections.emptyList();

		return queue;
	}

	private void prepare(ModInfo info) {
		queue.add(info);
		for (String mixin : info.getMixins()) {
			if (mixin.startsWith("@"))
				Mixins.addConfiguration(MixinConfigGenerator.PREFIX + mixin.substring(1));
			else
				Mixins.addConfiguration(mixin);
		}
	}

	public static Gson getGson(Mod mod) {
		GsonBuilder builder = new GsonBuilder();
		if (mod != null) {
            builder.registerTypeAdapter(mod.getClass(), (InstanceCreator<Mod>) (type) -> mod);
            mod.registerTypeAdapters(builder);
        }

		return builder.excludeFieldsWithoutExposeAnnotation().create();
	}

}
