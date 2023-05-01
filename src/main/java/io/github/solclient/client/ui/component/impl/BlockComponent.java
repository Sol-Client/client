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

import io.github.solclient.client.ui.component.ComponentRenderInfo;
import io.github.solclient.client.ui.component.controller.Controller;
import io.github.solclient.client.util.data.Colour;

public class BlockComponent extends ColouredComponent {

	private Controller<Float> radius;
	private Controller<Float> strokeWidth;

	public BlockComponent(Colour colour) {
		this(colour, 0, 0);
	}

	public BlockComponent(Colour colour, float radius, float strokeWidth) {
		this((component, defaultColour) -> colour, (component, defaultRadius) -> radius,
				(component, defaultstrokeWidth) -> strokeWidth);
	}

	public BlockComponent(Controller<Colour> colour, Controller<Float> radius, Controller<Float> strokeWidth) {
		super(colour);
		this.radius = radius;
		this.strokeWidth = strokeWidth;
	}

	@Override
	public void render(ComponentRenderInfo info) {
		float radius = this.radius.get(this);

		float strokeWidth = this.strokeWidth.get(this, 0F);
		boolean stroke = strokeWidth > 0;

		NanoVG.nvgBeginPath(nvg);

		if (stroke)
			NanoVG.nvgStrokeColor(nvg, getColour().nvg());
		else
			NanoVG.nvgFillColor(nvg, getColour().nvg());

		NanoVG.nvgStrokeWidth(nvg, strokeWidth);

		if (stroke)
			NanoVG.nvgRoundedRect(nvg, strokeWidth / 2, strokeWidth / 2, getBounds().getWidth() - strokeWidth,
					getBounds().getHeight() - strokeWidth, radius);
		else
			NanoVG.nvgRoundedRect(nvg, 0, 0, getBounds().getWidth(), getBounds().getHeight(), radius);

		if (stroke)
			NanoVG.nvgStroke(nvg);
		else
			NanoVG.nvgFill(nvg);

		super.render(info);
	}

}
