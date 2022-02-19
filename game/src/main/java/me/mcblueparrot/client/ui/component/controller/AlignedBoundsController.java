package me.mcblueparrot.client.ui.component.controller;
import lombok.AllArgsConstructor;
import me.mcblueparrot.client.ui.component.Component;
import me.mcblueparrot.client.util.data.Alignment;
import me.mcblueparrot.client.util.data.Rectangle;

@AllArgsConstructor
public class AlignedBoundsController implements Controller<Rectangle> {

	private Alignment xAlignment;
	private Alignment yAlignment;
	private Controller<Rectangle> baseController;

	public AlignedBoundsController(Alignment xAlignment, Alignment yAlightment) {
		this(xAlignment, xAlignment, (component, defaultBounds) -> defaultBounds);
	}

	@Override
	public Rectangle get(Component component, Rectangle defaultBounds) {
		return baseController.get(component,
				new Rectangle(xAlignment.getPosition(component.getParent().getBounds().getWidth(), defaultBounds.getWidth()),
						yAlignment.getPosition(component.getParent().getBounds().getHeight(), defaultBounds.getHeight()),
						defaultBounds.getWidth(), defaultBounds.getHeight()));
	}

}
