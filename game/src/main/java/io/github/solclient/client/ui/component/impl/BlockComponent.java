package io.github.solclient.client.ui.component.impl;

import io.github.solclient.client.ui.component.Component;
import io.github.solclient.client.ui.component.ComponentRenderInfo;
import io.github.solclient.client.ui.component.controller.Controller;
import io.github.solclient.client.util.data.Colour;
import io.github.solclient.client.util.data.Rectangle;

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
