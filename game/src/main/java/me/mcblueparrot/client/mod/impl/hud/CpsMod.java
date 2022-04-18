package me.mcblueparrot.client.mod.impl.hud;

import com.google.gson.annotations.Expose;
import com.replaymod.lib.de.johni0702.minecraft.gui.container.AbstractGuiScreen.Background;

import me.mcblueparrot.client.CpsMonitor;
import me.mcblueparrot.client.mod.annotation.Option;
import me.mcblueparrot.client.mod.hud.SimpleHudMod;
import me.mcblueparrot.client.util.Utils;
import me.mcblueparrot.client.util.data.Colour;
import me.mcblueparrot.client.util.data.Position;

public class CpsMod extends SimpleHudMod {

	@Expose
	@Option
	private boolean rmb;
	@Expose
	@Option
	private Colour separatorColour = new Colour(64, 64, 64);

	@Override
	public String getId() {
		return "cps";
	}

	@Override
	public void render(Position position, boolean editMode) {
		super.render(position, editMode);
		if(rmb) {
			String prefix = background ? "" : "[";
			String suffix = background ? "" : "]";

			int width = font.getStringWidth(prefix + CpsMonitor.LMB.getCps() + " | " + CpsMonitor.RMB.getCps() + " CPS" + suffix) - 2;

			int x = position.getX() + (53 / 2) - (width / 2);
			int y = position.getY() + 4;

			x = font.drawString(prefix + Integer.toString(CpsMonitor.LMB.getCps()), x, y, textColour.getValue(), shadow);

			x--;
			if(shadow) x--;

			x += font.getCharWidth(' ');

			Utils.drawVerticalLine(x, y - 1, y + 7, separatorColour.getValue());

			if(shadow) {
				Utils.drawVerticalLine(x + 1, y, y + 8, separatorColour.getShadowValue());
			}

			x += 1;

			x += font.getCharWidth(' ');

			font.drawString(CpsMonitor.RMB.getCps() + " CPS" + suffix, x, y, textColour.getValue(), shadow);
		}
	}

	@Override
	public String getText(boolean editMode) {
		return rmb ? "" : CpsMonitor.LMB.getCps() + " CPS";
	}

}
