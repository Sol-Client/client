package io.github.solclient.api.option;

import org.jetbrains.annotations.NotNull;

public interface Options {

	float mouseSensitivity();

	boolean invertMouse();

	@NotNull KeyBinding[] getKeys();

	void addKey(@NotNull KeyBinding key);

	void removeKey(@NotNull KeyBinding key);

}
