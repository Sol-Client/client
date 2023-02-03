package io.github.solclient.client.ui.component.impl;

import org.lwjgl.nanovg.NanoVG;

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


		if (getColour().needsOutline(theme.bg)) {
			NanoVG.nvgStrokeColor(nvg, Colour.WHITE.nvg());
			NanoVG.nvgStrokeWidth(nvg, 1);
			NanoVG.nvgStroke(nvg);
		}

		super.render(info);
	}

}
