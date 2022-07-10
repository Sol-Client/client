package io.github.solclient.client.platform.mc.option;

import org.jetbrains.annotations.NotNull;

import io.github.solclient.client.platform.Helper;

public interface Options {

	float mouseSensitivity();

	void setMouseSensitivity(float sensitivity);

	boolean invertMouse();

	boolean debugOverlay();

	@NotNull Perspective perspective();

	int ordinalPerspective();

	void setPerspective(@NotNull Perspective perspective);

	void setOrdinalPerspective(int perspective);

	@NotNull KeyBinding[] keys();

	@NotNull KeyBinding forwardsKey();

	@NotNull KeyBinding backwardsKey();

	@NotNull KeyBinding strafeLeftKey();

	@NotNull KeyBinding strafeRightKey();

	@NotNull KeyBinding attackKey();

	@NotNull KeyBinding useKey();

	@NotNull KeyBinding jumpKey();

	@NotNull KeyBinding sprintKey();

	void overwriteSprintKey(@NotNull KeyBinding sprint);

	boolean hideGui();

	@Helper
	void addKey(@NotNull KeyBinding key);

	@Helper
	void removeKey(@NotNull KeyBinding key);

	boolean smoothCamera();

	void setSmoothCamera(boolean camera);

}
