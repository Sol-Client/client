package io.github.solclient.client.mod;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

import com.google.gson.*;

import io.github.solclient.client.Client;

public final class PinManager {

	private final LinkedList<Mod> pinnedMods = new LinkedList<>();

	public void load(Path file) throws FileNotFoundException, IOException {
		if (!Files.exists(file))
			return;

		pinnedMods.clear();
		Client.INSTANCE.getMods().forEach(Mod::notifyUnpin);

		try (Reader reader = new InputStreamReader(Files.newInputStream(file), StandardCharsets.UTF_8)) {
			JsonArray array = JsonParser.parseReader(reader).getAsJsonArray();
			array.forEach((mod) -> addById(mod.getAsString()));
		}
	}

	public void save(Path file) throws IOException {
		// dirty...
		try (Writer writer = new OutputStreamWriter(Files.newOutputStream(file), StandardCharsets.UTF_8)) {
			int i = 0;
			writer.write('[');
			for (Mod mod : pinnedMods) {
				if (i != 0) {
					writer.write(',');
				}

				writer.write(new JsonPrimitive(mod.getId()).toString());
				i++;
			}
			writer.write(']');
		}
	}

	private void addById(String id) {
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

	public void reorder(Mod mod, int newIndex) {
		pinnedMods.remove(mod);
		pinnedMods.add(newIndex, mod);
	}

	public List<Mod> getMods() {
		return pinnedMods;
	}

}
