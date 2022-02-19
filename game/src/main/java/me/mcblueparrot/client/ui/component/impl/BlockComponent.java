package me.mcblueparrot.client.ui.component.impl;

import me.mcblueparrot.client.ui.component.Component;
import me.mcblueparrot.client.ui.component.ComponentRenderInfo;
import me.mcblueparrot.client.ui.component.controller.Controller;
import me.mcblueparrot.client.util.data.Colour;
import me.mcblueparrot.client.util.data.Rectangle;

public class BlockComponent extends ColouredComponent {

	public BlockComponent(Colour colour) {
		super((component, defaultColour) -> colour);
	}

	public BlockComponent(Controller<Colour> colour) {
		super(colour);
	}

	@Override
	public void render(ComponentRenderInfo info) {
		super.render(info);

		Rectangle.ofDimensions(getBounds().getWidth(), getBounds().getHeight()).fill(getColour());
	}

}
