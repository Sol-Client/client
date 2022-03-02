package me.mcblueparrot.client.ui.element;

import me.mcblueparrot.client.mod.impl.SolClientMod;
import me.mcblueparrot.client.util.Utils;
import me.mcblueparrot.client.util.data.Colour;
import me.mcblueparrot.client.util.data.Rectangle;

public class Tickbox {

	private Rectangle bounds;
	private boolean value;

	public Tickbox(int x, int y, boolean value) {
		this.bounds = new Rectangle(x, y, 15, 15);
		this.value = value;
	}

	public void render(int mouseX, int mouseY, boolean hovered) {
		Colour boxColour = hovered ? SolClientMod.instance.uiHover : SolClientMod.instance.uiColour;
		bounds.stroke(boxColour);

		if(value) {
			Utils.drawRectangle(new Rectangle(bounds.getX() + 2, bounds.getY() + 2, bounds.getWidth() - 4,
					bounds.getHeight() - 4), boxColour);
		}
	}

	public boolean contains(int x, int y) {
		return bounds.contains(x, y);
	}

}
