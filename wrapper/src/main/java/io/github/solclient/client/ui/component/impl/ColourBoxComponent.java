package io.github.solclient.client.ui.component.impl;

import org.lwjgl.nanovg.NanoVG;

import io.github.solclient.client.mod.impl.SolClientConfig;
import io.github.solclient.client.ui.component.*;
import io.github.solclient.client.ui.component.controller.*;
import io.github.solclient.client.util.data.*;

public class ColourBoxComponent extends ColouredComponent {

	private final Controller<Colour> outlineController = new AnimatedColourController(
			(component, defaultColour) -> isHovered() ? Colour.LIGHT_BUTTON_HOVER : Colour.LIGHT_BUTTON);

	public ColourBoxComponent(Controller<Colour> colour) {
		super(colour);
	}

	@Override
	protected Rectangle getDefaultBounds() {
		return Rectangle.ofDimensions(16, 16);
	}

	@Override
	public void render(ComponentRenderInfo info) {
		float radius = 0;

		if (SolClientConfig.instance.roundedUI)
			radius = getBounds().getHeight();

		NanoVG.nvgBeginPath(nvg);
		NanoVG.nvgFillColor(nvg, getColour().nvg());
		NanoVG.nvgRoundedRect(nvg, 0, 0, getBounds().getWidth(), getBounds().getHeight(), radius);
		NanoVG.nvgFill(nvg);

		NanoVG.nvgBeginPath(nvg);
		NanoVG.nvgStrokeColor(nvg, outlineController.get(this, null).nvg());
		NanoVG.nvgStrokeWidth(nvg, 1);
		NanoVG.nvgRoundedRect(nvg, 0, 0, getBounds().getWidth(), getBounds().getHeight(), radius);
		NanoVG.nvgStroke(nvg);

		super.render(info);
	}

}
