package io.github.solclient.abstraction.mc.option;

import org.jetbrains.annotations.NotNull;

public interface Options {

	float mouseSensitivity();

	boolean invertMouse();

	boolean debugOverlay();

	@NotNull Perspective perspective();

	@NotNull KeyBinding[] keys();

	@NotNull KeyBinding forwardsKey();

	@NotNull KeyBinding backwardsKey();

	@NotNull KeyBinding strafeLeftKey();

	@NotNull KeyBinding strafeRightKey();

	@NotNull KeyBinding attackKey();

	@NotNull KeyBinding useKey();

	@NotNull KeyBinding jumpKey();

	void addKey(@NotNull KeyBinding key);

	void removeKey(@NotNull KeyBinding key);

}
