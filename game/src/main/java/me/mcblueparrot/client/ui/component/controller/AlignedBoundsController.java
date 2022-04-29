package me.mcblueparrot.client.ui.component.controller;

import lombok.AllArgsConstructor;
import me.mcblueparrot.client.ui.component.Component;
import me.mcblueparrot.client.ui.component.impl.ButtonComponent;
import me.mcblueparrot.client.ui.component.impl.LabelComponent;
import me.mcblueparrot.client.util.data.Alignment;
import me.mcblueparrot.client.util.data.Rectangle;

@AllArgsConstructor
public class AlignedBoundsController implements Controller<Rectangle> {

	private final Alignment xAlignment;
	private final Alignment yAlignment;
	private final Controller<Rectangle> baseController;

	public AlignedBoundsController(Alignment xAlignment, Alignment yAlignment) {
		this(xAlignment, yAlignment, (component, defaultBounds) -> defaultBounds);
	}

	@Override
	public Rectangle get(Component component, Rectangle defaultBounds) {
		return baseController.get(component,
				new Rectangle(xAlignment.getPosition(component.getParent().getBounds().getWidth(), defaultBounds.getWidth()),
						yAlignment.getPosition(component.getParent().getBounds().getHeight(), defaultBounds.getHeight()),
						defaultBounds.getWidth(), defaultBounds.getHeight()));
	}

}
