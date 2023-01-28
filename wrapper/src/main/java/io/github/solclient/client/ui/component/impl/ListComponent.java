package io.github.solclient.client.ui.component.impl;

import io.github.solclient.client.ui.component.Component;
import io.github.solclient.client.ui.component.controller.AlignedBoundsController;
import io.github.solclient.client.util.data.*;

public abstract class ListComponent extends Component {

	public void add(Component component) {
		add(getSubComponents().size(), component);
	}

	public void add(int index, Component component) {
		add(index, component,
				new AlignedBoundsController(Alignment.CENTRE, Alignment.START, (sizingComponent, defaultBounds) -> {
					Component previous = null;
					Rectangle lastBounds = null;

					int prevIndex = subComponents.indexOf(component) - 1;
					if (prevIndex > -1) {
						previous = subComponents.get(prevIndex);
						lastBounds = previous.getCachedBounds();
					}

					return new Rectangle(defaultBounds.getX(),
							previous == null ? 0 : lastBounds.getY() + lastBounds.getHeight() + getSpacing(),
							defaultBounds.getWidth(), defaultBounds.getHeight());
				}));
	}

	protected int getContentHeight() {
		if (subComponents.isEmpty())
			return 0;

		return getBounds(subComponents.get(subComponents.size() - 1)).getEndY();
	}


	@Override
	protected boolean shouldCull(Component component) {
		return component.getBounds().getEndY() < 0 || component.getBounds().getY() > getBounds().getHeight();
	}

	public abstract int getSpacing();

}
