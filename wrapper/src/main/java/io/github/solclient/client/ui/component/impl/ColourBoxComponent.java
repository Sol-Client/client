package io.github.solclient.client.ui.component.impl;

import org.lwjgl.nanovg.NanoVG;

import io.github.solclient.client.ui.component.ComponentRenderInfo;
import io.github.solclient.client.ui.component.controller.*;
import io.github.solclient.client.util.data.*;

public class ColourBoxComponent extends ColouredComponent {

	private final Controller<Colour> outlineController = new AnimatedColourController(
			(component, defaultColour) -> isHovered() || getColour().getAlpha() <= 50 ? theme.fg : Colour.TRANSPARENT, 100);

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
		NanoVG.nvgCircle(nvg, getBounds().getWidth() / 2, getBounds().getHeight() / 2, getBounds().getWidth());
		NanoVG.nvgFill(nvg);

		NanoVG.nvgBeginPath(nvg);
		NanoVG.nvgStrokeColor(nvg, outlineController.get(this, null).nvg());
		NanoVG.nvgStrokeWidth(nvg, 1);
		NanoVG.nvgCircle(nvg, getBounds().getWidth() / 2, getBounds().getHeight() / 2, getBounds().getWidth() + 1);
		NanoVG.nvgStroke(nvg);

		super.render(info);
	}

}
