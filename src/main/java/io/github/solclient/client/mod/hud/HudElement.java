/*
 * Sol Client - an open source Minecraft client
 * Copyright (C) 2021-2023  TheKodeToad and Contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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
