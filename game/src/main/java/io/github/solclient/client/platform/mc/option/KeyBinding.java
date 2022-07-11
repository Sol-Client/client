package io.github.solclient.client.platform.mc.option;

import java.util.List;

import org.jetbrains.annotations.NotNull;

public interface KeyBinding {

	static @NotNull KeyBinding create(@NotNull String name, int initialKey, @NotNull String category) {
		throw new UnsupportedOperationException();
	}

	@NotNull String getCategory();

	@NotNull String getName();

	int getKeyCode();

	void setKeyCode(int code);

	boolean isHeld();

	boolean consumePress();

	default void clearPresses() {
		while(consumePress());
	}

	@NotNull List<KeyBinding> getConflictingKeys();

	static void reload() {
		throw new UnsupportedOperationException();
	}

}
