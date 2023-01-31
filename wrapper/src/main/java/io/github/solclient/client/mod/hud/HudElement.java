package io.github.solclient.client.mod.hud;

import io.github.solclient.client.mod.Mod;
import io.github.solclient.client.util.data.*;

/**
 * Represents a HUD element. May be contained inside a mod.
 */
public interface HudElement {

	default void move(int x, int y) {
		float[] position = getHighPrecisionPosition();
		position[0] += x;
		position[1] += y;
		setHighPrecisionPosition(position);
	}

	Position getPosition();

	float[] getHighPrecisionPosition();

	void setHighPrecisionPosition(float[] position);

	Position getDividedPosition();

	HudPosition getDefaultPosition();

	void setPosition(Position position);

	boolean isVisible();

	Rectangle getBounds();

	Rectangle getBounds(Position position);

	float[] getHighPrecisionMultipliedBounds();

	Rectangle getMultipliedBounds();

	void render(boolean editMode);

	void render(Position position, boolean editMode);

	Mod getMod();

	boolean isHovered(int mouseX, int mouseY);

	boolean isShownInReplay();

}
