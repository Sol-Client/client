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

package io.github.solclient.client.ui.component.impl;

import org.lwjgl.nanovg.NanoVG;

import io.github.solclient.client.ui.Theme;
import io.github.solclient.client.ui.component.ComponentRenderInfo;
import io.github.solclient.client.ui.component.controller.*;
import io.github.solclient.client.util.data.*;

public class ColourBoxComponent extends ColouredComponent {

	public ColourBoxComponent(Controller<Colour> colour) {
		super(colour);
	}

	@Override
	protected Rectangle getDefaultBounds() {
		return Rectangle.ofDimensions(16, 16);
	}

	@Override
	public void render(ComponentRenderInfo info) {
		NanoVG.nvgBeginPath(nvg);
		NanoVG.nvgFillColor(nvg, getColour().nvg());
		NanoVG.nvgCircle(nvg, getBounds().getWidth() / 2, getBounds().getHeight() / 2, getBounds().getWidth() / 2);
		NanoVG.nvgFill(nvg);


		if (getColour().needsOutline(Theme.getCurrent().bg)) {
			NanoVG.nvgStrokeColor(nvg, Theme.getCurrent().fg.nvg());
			NanoVG.nvgStrokeWidth(nvg, 1);
			NanoVG.nvgStroke(nvg);
		}

		super.render(info);
	}

}
