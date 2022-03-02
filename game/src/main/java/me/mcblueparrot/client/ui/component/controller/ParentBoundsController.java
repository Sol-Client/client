package me.mcblueparrot.client.ui.component.controller;

import me.mcblueparrot.client.ui.component.Component;
import me.mcblueparrot.client.util.data.Rectangle;

public class ParentBoundsController implements Controller<Rectangle> {

	@Override
	public Rectangle get(Component component, Rectangle defaultBounds) {
		return component.getParent().getBounds();
	}

}
