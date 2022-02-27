package me.mcblueparrot.client.ui.component.impl;

import me.mcblueparrot.client.ui.component.Component;
import me.mcblueparrot.client.ui.component.controller.AnimatedColourController;
import me.mcblueparrot.client.ui.component.controller.Controller;
import me.mcblueparrot.client.util.data.Colour;

public class ColourBoxComponent extends ScaledIconComponent {

	private Component hoverController;

	public ColourBoxComponent(Controller<Colour> colourController, Component hoverController) {
		super("sol_client_colour_fill", 16, 16,
				new AnimatedColourController(
						(component, defaultColour) -> colourController.get(component, defaultColour)));

		this.hoverController = hoverController;

		add(new ScaledIconComponent("sol_client_colour_circle", 16, 16, new AnimatedColourController(
				(component, defaultColour) -> isHovered() ? Colour.LIGHT_BUTTON_HOVER : Colour.LIGHT_BUTTON)),
				(component, defaultBounds) -> defaultBounds);
	}

	@Override
	public boolean isHovered() {
		return hoverController.isHovered();
	}

}
