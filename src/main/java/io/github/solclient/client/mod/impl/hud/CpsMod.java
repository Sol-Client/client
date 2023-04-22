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

import io.github.solclient.client.mod.impl.SolClientSimpleHudMod;
import io.github.solclient.client.mod.impl.core.CpsMonitor;
import io.github.solclient.client.mod.option.annotation.Option;
import io.github.solclient.client.util.MinecraftUtils;
import io.github.solclient.client.util.data.*;

public class CpsMod extends SolClientSimpleHudMod {

	@Expose
	@Option
	private boolean rmb;
	@Expose
	@Option
	private Colour separatorColour = new Colour(64, 64, 64);

	@Override
	public void render(Position position, boolean editMode) {
		super.render(position, editMode);
		if (rmb) {
			String prefix = background ? "" : "[";
			String suffix = background ? "" : "]";

			int width = font.getStringWidth(
					prefix + CpsMonitor.LMB.getCps() + " | " + CpsMonitor.RMB.getCps() + " CPS" + suffix) - 2;

			int x = position.getX() + (53 / 2) - (width / 2);
			int y = position.getY() + 4;

			x = font.draw(prefix + Integer.toString(CpsMonitor.LMB.getCps()), x, y, textColour.getValue(), shadow);

			x--;
			if (shadow)
				x--;

			x += font.getCharWidth(' ');

			MinecraftUtils.drawVerticalLine(x, y - 1, y + 7, separatorColour.getValue());

			if (shadow) {
				MinecraftUtils.drawVerticalLine(x + 1, y, y + 8, separatorColour.getShadowValue());
			}

			x += 1;

			x += font.getCharWidth(' ');

			font.draw(CpsMonitor.RMB.getCps() + " CPS" + suffix, x, y, textColour.getValue(), shadow);
		}
	}

	@Override
	public String getText(boolean editMode) {
		return rmb ? "" : CpsMonitor.LMB.getCps() + " CPS";
	}

}
