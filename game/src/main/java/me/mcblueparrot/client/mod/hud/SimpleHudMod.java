package me.mcblueparrot.client.mod.hud;

import com.google.gson.annotations.Expose;

import me.mcblueparrot.client.mod.annotation.ConfigOption;
import me.mcblueparrot.client.util.data.Colour;
import me.mcblueparrot.client.util.data.Position;
import me.mcblueparrot.client.util.data.Rectangle;

public abstract class SimpleHudMod extends HudMod {

	@Expose
	@ConfigOption("Background")
	protected boolean background = true;
	@Expose
	@ConfigOption("Background Colour")
	private Colour backgroundColour = new Colour(0, 0, 0, 100);
	@Expose
	@ConfigOption("Border")
	private boolean border = false;
	@Expose
	@ConfigOption("Border Colour")
	private Colour borderColour = Colour.BLACK;
	@Expose
	@ConfigOption("Text Colour")
	protected Colour textColour = Colour.WHITE;
	@Expose
	@ConfigOption("Text Shadow")
	protected boolean shadow = true;

	public SimpleHudMod(String name, String id, String description) {
		super(name, id, description);
	}

	@Override
	public Rectangle getBounds(Position position) {
		return new Rectangle(position.getX(), position.getY(), 53, 16);
	}

	@Override
	public void render(Position position, boolean editMode) {
		String text = getText(editMode);
		if(text != null) {
			if(background) {
				getBounds(position).fill(backgroundColour);
			}
			else {
				if(!text.isEmpty()) {
					text = "[" + text + "]";
				}
			}

			if(border) {
				getBounds(position).stroke(borderColour);
			}
 			font.drawString(text,
					position.getX() + (getBounds(position).getWidth() / 2F) - (font.getStringWidth(text) / 2F),
					position.getY() + 4, textColour.getValue(), shadow);
		}
	}

	public abstract String getText(boolean editMode);

}
