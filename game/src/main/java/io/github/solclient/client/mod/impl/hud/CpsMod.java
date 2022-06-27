package io.github.solclient.client.mod.impl.hud;

import com.google.gson.annotations.Expose;

import io.github.solclient.abstraction.mc.DrawableHelper;
import io.github.solclient.client.CpsCounter;
import io.github.solclient.client.mod.annotation.Option;
import io.github.solclient.client.mod.hud.SimpleHudMod;
import io.github.solclient.client.util.Utils;
import io.github.solclient.client.util.data.Colour;
import io.github.solclient.client.util.data.Position;

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

			int width = font.getWidth(prefix + CpsCounter.LMB.getCps() + " | " + CpsCounter.RMB.getCps() + " CPS" + suffix) - 2;

			int x = position.getX() + (53 / 2) - (width / 2);
			int y = position.getY() + 4;

			x = font.render(prefix + Integer.toString(CpsCounter.LMB.getCps()), x, y, textColour.getValue(), shadow);

			x--;
			if(shadow) x--;

			x += font.getWidth(' ');

			DrawableHelper.renderVerticalLine(x, y - 1, y + 7, separatorColour.getValue());

			if(shadow) {
				DrawableHelper.renderVerticalLine(x + 1, y, y + 8, separatorColour.getShadowValue());
			}

			x += 1;

			x += font.getWidth(' ');

			font.render(CpsCounter.RMB.getCps() + " CPS" + suffix, x, y, textColour.getValue(), shadow);
		}
	}

	@Override
	public String getText(boolean editMode) {
		return rmb ? "" : CpsCounter.LMB.getCps() + " CPS";
	}

}
