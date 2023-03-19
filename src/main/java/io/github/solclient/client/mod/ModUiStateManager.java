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

package io.github.solclient.client.mod;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

import com.google.gson.*;

import io.github.solclient.client.Client;
import io.github.solclient.client.util.WrappingLinkedList;
import io.github.solclient.client.util.data.Colour;
import lombok.Getter;

public final class ModUiStateManager {

	@Getter
	private final List<Mod> pins = new LinkedList<>();
	@Getter
	private final LinkedList<Colour> previousColours = new WrappingLinkedList<>(Colour.TRANSPARENT, 10);
	private final Set<ModCategory> collapsedCategories = new HashSet<>();

	public void load(Path file) throws FileNotFoundException, IOException {
		if (!Files.exists(file))
			return;

		pins.clear();
		Client.INSTANCE.getMods().forEach(Mod::notifyUnpin);

		try (Reader reader = new InputStreamReader(Files.newInputStream(file), StandardCharsets.UTF_8)) {
			JsonObject object = new JsonParser().parse(reader).getAsJsonObject();

			if (object.has("pins"))
				object.get("pins").getAsJsonArray().forEach((mod) -> addPinById(mod.getAsString()));

			if (object.has("collapsedCategories"))
				object.get("collapsedCategories").getAsJsonArray().forEach((category) -> {
					try {
						collapsedCategories.add(ModCategory.valueOf(category.getAsString()));
					} catch (IllegalArgumentException ignored) {
					}
				});

			if (object.has("previousColours"))
				object.get("previousColours").getAsJsonArray()
						.forEach((colour) -> previousColours.add(new Colour(colour.getAsInt())));
		}
	}

	public void save(Path file) throws IOException {
		JsonObject result = new JsonObject();

		JsonArray pins = new JsonArray();
		this.pins.forEach((mod) -> pins.add(new JsonPrimitive(mod.getId())));
		result.add("pins", pins);

		JsonArray collapsedCategories = new JsonArray();
		this.collapsedCategories.forEach((category) -> collapsedCategories.add(new JsonPrimitive(category.name())));
		result.add("collapsedCategories", collapsedCategories);

		JsonArray previousColours = new JsonArray();
		this.previousColours.forEach((colour) -> previousColours.add(new JsonPrimitive(colour.getValue())));
		result.add("previousColours", previousColours);

		try (Writer writer = new OutputStreamWriter(Files.newOutputStream(file), StandardCharsets.UTF_8)) {
			writer.write(result.toString());
		}
	}

	private void addPinById(String id) {
		Client.INSTANCE.getMods().getById(id).ifPresent((mod) -> {
			pins.add(mod);
			mod.notifyPin();
		});
	}

	boolean determinePinState(Mod mod) {
		return pins.contains(mod);
	}

	void notifyPin(Mod mod) {
		pins.add(mod);
	}

	void notifyUnpin(Mod mod) {
		pins.remove(mod);
	}

	public void reorderPin(Mod mod, int newIndex) {
		pins.remove(mod);
		pins.add(newIndex, mod);
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
