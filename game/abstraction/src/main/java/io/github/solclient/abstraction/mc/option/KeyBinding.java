package io.github.solclient.abstraction.mc.option;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import io.github.solclient.abstraction.mc.MinecraftClient;

import java.util.List;

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

}
