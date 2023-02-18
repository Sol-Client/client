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

package io.github.solclient.client.mod.impl.hud;

import com.google.gson.annotations.Expose;

import io.github.solclient.client.mod.impl.*;
import io.github.solclient.client.mod.option.annotation.*;
import io.github.solclient.client.util.data.*;
import net.minecraft.util.math.MathHelper;

public class CoordinatesMod extends SolClientHudMod {

	@Expose
	@Option(translationKey = SolClientSimpleHudMod.TRANSLATION_KEY)
	private boolean background = true;
	@Expose
	@ColourKey(ColourKey.BACKGROUND_COLOUR)
	@Option(translationKey = SolClientSimpleHudMod.TRANSLATION_KEY)
	private Colour backgroundColour = new Colour(0, 0, 0, 100);
	@Expose
	@Option(translationKey = SolClientSimpleHudMod.TRANSLATION_KEY)
	private boolean border = false;
	@Expose
	@ColourKey(ColourKey.BORDER_COLOUR)
	@Option(translationKey = SolClientSimpleHudMod.TRANSLATION_KEY)
	private Colour borderColour = Colour.BLACK;
	@Expose
	@Option
	private Colour axisLabelColour = new Colour(0, 150, 255);
	@Expose
	@Option
	private Colour axisValueColour = Colour.WHITE;
	@Expose
	@Option
	private boolean cardinalDirection = true;
	@Expose
	@Option
	private Colour cardinalDirectionColour = new Colour(0, 150, 255);
	@Expose
	@Option
	private boolean axisDirection = true;
	@Expose
	@Option
	private Colour axisDirectionColour = new Colour(0, 150, 255);
	@Expose
	@Option(translationKey = SolClientSimpleHudMod.TRANSLATION_KEY)
	private boolean shadow = true;

	@Override
	public String getId() {
		return "coordinates";
	}

	@Override
	public Rectangle getBounds(Position position) {
		return new Rectangle(position.getX(), position.getY(), 82,
				4 + font.fontHeight + 2 + font.fontHeight + 2 + font.fontHeight + 2);
	}

	@Override
	public void render(Position position, boolean editMode) {
		int subtract = 0;
		if (shadow) {
			subtract++;
		}

		if (background) {
			getBounds(position).fill(backgroundColour);
		}

		if (border) {
			getBounds(position).stroke(borderColour);
		}

		double x, y, z, yaw;
		if (editMode) {
			x = 0;
			y = 0;
			z = 0;
			yaw = -90;
		} else {
			x = mc.player.x;
			y = mc.player.y;
			z = mc.player.z;
			yaw = mc.player.yaw;
		}
		int width = 80;
		int cardinalDirectionIndex = MathHelper.floor(((MathHelper.wrapDegrees(yaw) + 180D + 22.5D) % 360D) / 45D);
		String[] cardinalDirections = { "N", "NE", "E", "SE", "S", "SW", "W", "NW" };
		String xDirection = null;
		String zDirection = null;
		switch (cardinalDirectionIndex) {
			case 0:
				zDirection = "--";
				break;
			case 1:
				zDirection = "-";
				xDirection = "+";
				break;
			case 2:
				xDirection = "++";
				break;
			case 3:
				zDirection = "+";
				xDirection = "+";
				break;
			case 4:
				zDirection = "++";
				break;
			case 5:
				zDirection = "+";
				xDirection = "-";
				break;
			case 6:
				xDirection = "--";
				break;
			case 7:
				zDirection = "-";
				xDirection = "-";
				break;
		}
		String facing = cardinalDirections[cardinalDirectionIndex];
		font.draw(Integer.toString((int) x),
				font.draw("X ", position.getX() + 4, position.getY() + 4, axisLabelColour.getValue(), shadow)
						- subtract,
				position.getY() + 4, axisValueColour.getValue(), shadow);

		if (xDirection != null && axisDirection) {
			font.draw(xDirection, position.getX() + width - font.getStringWidth(xDirection) - 2, position.getY() + 4,
					axisDirectionColour.getValue(), shadow);
		}

		font.draw(Integer.toString((int) y),
				font.draw("Y ", position.getX() + 4, position.getY() + 4 + font.fontHeight + 2,
						axisLabelColour.getValue(), shadow) - subtract,
				position.getY() + 4 + font.fontHeight + 2, axisValueColour.getValue(), shadow);

		if (cardinalDirection) {
			font.draw(facing, position.getX() + width - font.getStringWidth(facing) - 2,
					position.getY() + 4 + font.fontHeight + 2, cardinalDirectionColour.getValue(), shadow);
		}

		font.draw(Integer.toString((int) z),
				font.draw("Z ", position.getX() + 4, position.getY() + 4 + font.fontHeight + 2 + font.fontHeight + 2,
						axisLabelColour.getValue(), shadow) - subtract,
				position.getY() + 4 + font.fontHeight + 2 + font.fontHeight + 2, axisValueColour.getValue(), shadow);

		if (zDirection != null && axisDirection) {
			font.draw(zDirection, position.getX() + width - font.getStringWidth(zDirection) - 2,
					position.getY() + 4 + font.fontHeight + 2 + font.fontHeight + 2, axisDirectionColour.getValue(),
					shadow);
		}

	}

}
