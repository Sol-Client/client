package io.github.solclient.client.mod;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

import com.google.gson.*;

import io.github.solclient.client.Client;

public final class ModUiStateManager {

	private final LinkedList<Mod> pinnedMods = new LinkedList<>();
	private final Set<ModCategory> collapsedCategories = new HashSet<>();

	public void load(Path file) throws FileNotFoundException, IOException {
		if (!Files.exists(file))
			return;

		pinnedMods.clear();
		Client.INSTANCE.getMods().forEach(Mod::notifyUnpin);

		try (Reader reader = new InputStreamReader(Files.newInputStream(file), StandardCharsets.UTF_8)) {
			JsonObject object = JsonParser.parseReader(reader).getAsJsonObject();

			if (object.has("pins"))
				object.get("pins").getAsJsonArray().forEach((mod) -> addPinById(mod.getAsString()));

			if (object.has("collapsedCategories"))
				object.get("collapsedCategories").getAsJsonArray().forEach((category) -> {
					try {
						collapsedCategories.add(ModCategory.valueOf(category.getAsString()));
					} catch (IllegalArgumentException ignored) {
					}
				});
		}
	}

	public void save(Path file) throws IOException {
		JsonObject result = new JsonObject();

		JsonArray pins = new JsonArray();
		pinnedMods.forEach((mod) -> pins.add(mod.getId()));
		result.add("pins", pins);

		JsonArray collapsedCategories = new JsonArray();
		this.collapsedCategories.forEach((category) -> collapsedCategories.add(category.name()));
		result.add("collapsedCategories", collapsedCategories);

		try (Writer writer = new OutputStreamWriter(Files.newOutputStream(file), StandardCharsets.UTF_8)) {
			writer.write(result.toString());
		}
	}

	private void addPinById(String id) {
		Client.INSTANCE.getMods().getById(id).ifPresent((mod) -> {
			pinnedMods.add(mod);
			mod.notifyPin();
		});
	}

	boolean determinePinState(Mod mod) {
		return pinnedMods.contains(mod);
	}

	void notifyPin(Mod mod) {
		pinnedMods.add(mod);
	}

	void notifyUnpin(Mod mod) {
		pinnedMods.remove(mod);
	}

	public void reorderPin(Mod mod, int newIndex) {
		pinnedMods.remove(mod);
		pinnedMods.add(newIndex, mod);
	}

	public List<Mod> getPins() {
		return pinnedMods;
	}

	public boolean isExpanded(ModCategory category) {
		return !collapsedCategories.contains(category);
	}

	public void setExpanded(ModCategory category, boolean expanded) {
		if (expanded)
			collapsedCategories.remove(category);
		else
			collapsedCategories.add(category);
	}

}
