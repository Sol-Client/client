package io.github.solclient.api.option;

import io.github.solclient.api.MinecraftClient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface KeyBinding {

	static @NotNull KeyBinding create(@NotNull String name, int initialKey, @NotNull String category) {
		throw new UnsupportedOperationException();
	}

	@NotNull String getCategory();

	@NotNull String getName();

	int getKeyCode();

	void setKeyCode(int code);

	@NotNull List<KeyBinding> getConflictingKeys();

}
