package io.github.solclient.client.ui.screen.mods;

import io.github.solclient.client.mod.impl.SolClientConfig;
import io.github.solclient.client.ui.component.impl.BlockComponent;
import io.github.solclient.client.util.data.*;

public class ModGhost extends BlockComponent {

	public ModGhost() {
		super((component, defaultColour) -> theme.buttonSecondary.add(10),
				(component, defaultRadius) -> SolClientConfig.instance.roundedUI ? 4 : 0F,
				(component, defaultOutlineWidth) -> defaultOutlineWidth);
	}

	@Override
	protected Rectangle getDefaultBounds() {
		return Rectangle.ofDimensions(230, 30);
	}

}
