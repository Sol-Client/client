package me.mcblueparrot.client.mod.hud;

import me.mcblueparrot.client.mod.Mod;
import me.mcblueparrot.client.util.data.Position;
import me.mcblueparrot.client.util.data.Rectangle;

/**
 * Represents a HUD element. May be contained inside a mod.
 */
public interface HudElement {

	Position getPosition();

	Position getDividedPosition();

	HudPosition getDefaultPosition();

	void setPosition(Position position);

	boolean isVisible();

	Rectangle getBounds();

	Rectangle getBounds(Position position);

	Rectangle getMultipliedBounds();

	void render(boolean editMode);

	void render(Position position, boolean editMode);

	Mod getMod();

	boolean isSelected(int mouseX, int mouseY);

	boolean isShownInReplay();

}
