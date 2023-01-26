package io.github.solclient.client.ui.screen.mods;

import io.github.solclient.client.ui.component.impl.BlockComponent;
import io.github.solclient.client.util.data.Rectangle;

public class ModGhost extends BlockComponent {

	public ModGhost() {
		super(theme.buttonSecondary.add(10), 4, 0);
	}

	@Override
	protected Rectangle getDefaultBounds() {
		return Rectangle.ofDimensions(230, 30);
	}

}
