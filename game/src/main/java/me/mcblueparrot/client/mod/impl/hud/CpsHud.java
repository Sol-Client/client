package me.mcblueparrot.client.mod.impl.hud;

import com.google.gson.annotations.Expose;

import me.mcblueparrot.client.CpsMonitor;
import me.mcblueparrot.client.mod.annotation.ConfigOption;
import me.mcblueparrot.client.mod.hud.SimpleHud;
import me.mcblueparrot.client.util.Colour;
import me.mcblueparrot.client.util.Position;
import me.mcblueparrot.client.util.Utils;

public class CpsHud extends SimpleHud {

	@Expose
	@ConfigOption("RMB")
	private boolean rmb;
	@Expose
	@ConfigOption("Separator Colour")
	private Colour separatorColour = new Colour(64, 64, 64);

	public CpsHud() {
		super("CPS", "cps", "Display your CPS (clicks per second).");
	}

	@Override
	public void render(Position position, boolean editMode) {
		super.render(position, editMode);
		if(rmb) {
			int width = font.getStringWidth(CpsMonitor.LMB.getCps() + " | " + CpsMonitor.RMB.getCps() + " CPS") - 2;

			int x = position.getX() + (53 / 2) - (width / 2);
			int y = position.getY() + 4;

			x = font.drawString(Integer.toString(CpsMonitor.LMB.getCps()), x, y, textColour.getValue(), shadow);

			x--;
			if(shadow) x--;

			x += font.getCharWidth(' ');

			Utils.drawVerticalLine(x, y - 1, y + 7, separatorColour.getValue());

			if(shadow) {
				Utils.drawVerticalLine(x + 1, y, y + 8, separatorColour.getShadowValue());
			}

			x += 1;

			x += font.getCharWidth(' ');

			font.drawString(CpsMonitor.RMB.getCps() + " CPS", x, y, textColour.getValue(), shadow);
		}
	}

	@Override
	public String getText(boolean editMode) {
		return rmb ? "" : CpsMonitor.LMB.getCps() + " CPS";
	}

}
