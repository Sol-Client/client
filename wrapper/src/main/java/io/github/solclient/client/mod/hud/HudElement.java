package io.github.solclient.client.mod.hud;

import com.mojang.blaze3d.platform.GlStateManager;
import com.replaymod.replay.ReplayModReplay;

import io.github.solclient.client.mod.Mod;
import io.github.solclient.client.util.data.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Window;

/**
 * Represents a HUD element. May be contained inside a mod.
 */
public interface HudElement {

	float getScale();

	Position getConfiguredPosition();

	default Position getPosition() {
		Position result = getConfiguredPosition();
		if (result == null) {
			Window window = new Window(MinecraftClient.getInstance());
			Position defaultPosition = determineDefaultPosition(window.getWidth(), window.getHeight());
			setPosition(defaultPosition);
			result = defaultPosition;
		}

		return result;
	}

	default Position getDividedPosition() {
		return new Position((int) (getPosition().getX() / getScale()),
				(int) (getPosition().getY() / getScale()));
	}

	default Position determineDefaultPosition(int width, int height) {
		return new Position(0, 0);
	}

	void setPosition(Position position);

	boolean isVisible();

	default Rectangle getBounds() {
		return getBounds(getPosition());
	}

	Rectangle getBounds(Position position);

	default Rectangle getMultipliedBounds() {
		Rectangle rectangle = getBounds();
		if (rectangle == null)
			return null;

		return rectangle.multiply(getScale());
	}

	default void render(boolean editMode) {
		// Don't render HUD in replay or if marked as invisible.
		if (!isVisible() || !(editMode || isShownInReplay() || ReplayModReplay.instance.getReplayHandler() == null))
			return;

		GlStateManager.pushMatrix();
		GlStateManager.scale(getScale(), getScale(), getScale());
		render(getDividedPosition(), editMode);
		GlStateManager.popMatrix();
	}

	void render(Position position, boolean editMode);

	Mod getMod();

	default boolean isHovered(int x, int y) {
		Rectangle bounds = getMultipliedBounds();
		return bounds != null && bounds.contains(x, y);
	}

	boolean isShownInReplay();

}
