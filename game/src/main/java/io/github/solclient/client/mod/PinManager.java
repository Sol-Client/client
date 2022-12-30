package io.github.solclient.client.mod;

import java.io.File;
import java.util.LinkedList;

public final class PinManager {

	private final LinkedList<Mod> pinnedMods;

	public PinManager(File file) {
		this.pinnedMods = new LinkedList<>();
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

}
