package me.mcblueparrot.client.ui.component.impl;

import me.mcblueparrot.client.ui.component.Component;
import me.mcblueparrot.client.ui.component.ComponentRenderInfo;
import me.mcblueparrot.client.ui.component.controller.AnimatedColourController;
import me.mcblueparrot.client.ui.component.controller.Controller;
import me.mcblueparrot.client.util.Utils;
import me.mcblueparrot.client.util.data.Colour;

public class ColourBoxComponent extends ScaledIconComponent {

	private final Component hoverController;

	public ColourBoxComponent(Controller<Colour> colourController, Component hoverController) {
		super("sol_client_colour_fill", 16, 16,
				new AnimatedColourController(
						(component, defaultColour) -> colourController.get(component, defaultColour)));

		this.hoverController = hoverController;

		add(new ScaledIconComponent("sol_client_colour_circle", 16, 16, new AnimatedColourController(
				(component, defaultColour) -> isHovered() ? Colour.LIGHT_BUTTON_HOVER : Colour.LIGHT_BUTTON)) {

			@Override
			public boolean useFallback() {
				return true;
			}

			@Override
			public void renderFallback(ComponentRenderInfo info) {
				Utils.drawOutline(getRelativeBounds(), getColour());
			}

		}, (component, defaultBounds) -> defaultBounds);
	}

	@Override
	public boolean isHovered() {
		return hoverController != null ? hoverController.isHovered() : super.isHovered();
	}

	@Override
	public boolean useFallback() {
		return true;
	}

	@Override
	public void renderFallback(ComponentRenderInfo info) {
		Utils.drawRectangle(getRelativeBounds(), getColour());
	}

}
