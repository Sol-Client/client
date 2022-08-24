package io.github.solclient.client.platform.mc.option;

import java.util.List;

import org.jetbrains.annotations.NotNull;

public interface KeyBinding {

	static @NotNull KeyBinding create(@NotNull String name, int initialKey, @NotNull String category) {
		throw new UnsupportedOperationException();
	}

	@NotNull String getKeyCategory();

	@NotNull String getName();

	int getKeyCode();

	boolean isHeld();

	boolean consumePress();

	default void clearPresses() {
		while(consumePress()) {
			;
		}
	}

	@NotNull List<KeyBinding> getConflictingKeys();

	boolean conflicts();

	static void reload() {
		throw new UnsupportedOperationException();
	}

}
