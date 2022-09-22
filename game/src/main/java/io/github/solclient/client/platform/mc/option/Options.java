package io.github.solclient.client.platform.mc.option;

import org.jetbrains.annotations.NotNull;

import io.github.solclient.client.platform.Helper;

public interface Options {

	double mouseSensitivity();

	void setMouseSensitivity(double sensitivity);

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

	void setMouseButton(@NotNull KeyBinding binding, int button);

	void setKey(@NotNull KeyBinding binding, int code, int scancode);

	void unbindKey(@NotNull KeyBinding binding);

	void save();

	/**
	 *
	 * @return Language code in lower-case format. For example <code>en_us</code>.
	 */
	@NotNull String languageCode();

}
