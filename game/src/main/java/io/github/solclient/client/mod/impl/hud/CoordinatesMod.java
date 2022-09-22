package io.github.solclient.client.mod.impl.hud;

import com.google.gson.annotations.Expose;

import io.github.solclient.client.mod.annotation.Option;
import io.github.solclient.client.mod.hud.HudMod;
import io.github.solclient.client.mod.hud.SimpleHudMod;
import io.github.solclient.client.util.Utils;
import io.github.solclient.client.util.data.Colour;
import io.github.solclient.client.util.data.Position;
import io.github.solclient.client.util.data.Rectangle;

public class CoordinatesMod extends HudMod {

	public static final CoordinatesMod INSTANCE = new CoordinatesMod();

	private static final String[] CARDINAL_DIRECTIONS = { "N", "NE", "E", "SE", "S", "SW", "W", "NW" };

	@Expose
	@Option(translationKey = SimpleHudMod.TRANSLATION_KEY)
	private boolean background = true;
	@Expose
	@Option(translationKey = SimpleHudMod.TRANSLATION_KEY, applyToAllClass = Option.BACKGROUND_COLOUR_CLASS)
	private Colour backgroundColour = new Colour(0, 0, 0, 100);
	@Expose
	@Option(translationKey = SimpleHudMod.TRANSLATION_KEY)
	private boolean border = false;
	@Expose
	@Option(translationKey = SimpleHudMod.TRANSLATION_KEY, applyToAllClass = Option.BORDER_COLOUR_CLASS)
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
	@Option(translationKey = SimpleHudMod.TRANSLATION_KEY)
	private boolean shadow = true;

	@Override
	public String getId() {
		return "coordinates";
	}

	@Override
	public Rectangle getBounds(Position position) {
		return new Rectangle(position.getX(), position.getY(), 82, 4 + font.getHeight() + 2 + font.getHeight() + 2 + font.getHeight() + 2);
	}

	@Override
	public void render(Position position, boolean editMode) {
		int subtract = 0;
		if(shadow) {
			subtract++;
		}

		if(background) {
			getBounds(position).fill(backgroundColour);
		}

		if(border) {
			getBounds(position).stroke(borderColour);
		}

		double x, y, z, yaw;
		if(editMode) {
			x = 0;
			y = 0;
			z = 0;
			yaw = -90;
		}
		else {
			x = mc.getPlayer().x();
			y = mc.getPlayer().y();
			z = mc.getPlayer().z();
			yaw = mc.getPlayer().yaw();
		}

		int width = 80;
		int cardinalDirectionIndex = (int) Math.floor(
				((Utils.wrapYaw(yaw) + 180D + 22.5) % 360D) / 45D);

		String xDirection = null;
		String zDirection = null;

		switch(cardinalDirectionIndex) {
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

		String facing = CARDINAL_DIRECTIONS[cardinalDirectionIndex];

		font.render(Integer.toString((int) x),
				font.render("X ", position.getX() + 4, position.getY() + 4, axisLabelColour.getValue(), shadow) - subtract,
				position.getY() + 4, axisValueColour.getValue(), shadow);

		if(xDirection != null && axisDirection) {
			font.render(xDirection, position.getX() + width - font.getTextWidth(xDirection) - 2,
					position.getY() + 4,
					axisDirectionColour.getValue(), shadow);
		}

		font.render(Integer.toString((int) y),
				font.render("Y ", position.getX() + 4, position.getY() + 4 + font.getHeight() + 2,
						axisLabelColour.getValue(),
						shadow) - subtract,
				position.getY() + 4 + font.getHeight() + 2, axisValueColour.getValue(), shadow);

		if(cardinalDirection) {
			font.render(facing, position.getX() + width - font.getTextWidth(facing) - 2,
					position.getY() + 4 + font.getHeight() + 2, cardinalDirectionColour.getValue(), shadow);
		}

		font.render(Integer.toString((int) z),
				font.render("Z ", position.getX() + 4,
						position.getY() + 4 + font.getHeight() + 2 + font.getHeight() + 2,
						axisLabelColour.getValue(), shadow) - subtract,
				position.getY() + 4 + font.getHeight() + 2 + font.getHeight() + 2, axisValueColour.getValue(), shadow);

		if(zDirection != null && axisDirection) {
			font.render(zDirection, position.getX() + width - font.getTextWidth(zDirection) - 2,
					position.getY() + 4 + font.getHeight() + 2 + font.getHeight() + 2, axisDirectionColour.getValue(),
					shadow);
		}

	}

}
