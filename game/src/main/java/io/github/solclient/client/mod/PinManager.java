package io.github.solclient.client.mod;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

import com.google.gson.*;

import io.github.solclient.client.Client;

public final class PinManager {

	private final LinkedList<Mod> pinnedMods;

	public PinManager() {
		this.pinnedMods = new LinkedList<>();
	}

	public void load(File file) throws FileNotFoundException, IOException {
		if (!file.exists()) {
			return;
		}

		pinnedMods.clear();
		Client.INSTANCE.getMods().forEach(Mod::notifyUnpin);

		try (InputStream in = new FileInputStream(file)) {
			JsonArray array = JsonParser.parseReader(new InputStreamReader(in, StandardCharsets.UTF_8))
					.getAsJsonArray();
			array.forEach((mod) -> addById(mod.getAsString()));
		}
	}

	public void save(File file) throws IOException {
		// dirty...
		try (Writer writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
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
		Mod mod = Client.INSTANCE.getModById(id);
		if (mod == null) {
			return;
		}

		pinnedMods.add(mod);
		mod.notifyPin();
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
