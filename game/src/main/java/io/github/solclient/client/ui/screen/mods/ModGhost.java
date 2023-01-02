package io.github.solclient.client.ui.screen.mods;

import io.github.solclient.client.mod.impl.SolClientMod;
import io.github.solclient.client.ui.component.impl.BlockComponent;
import io.github.solclient.client.util.data.*;

public class ModGhost extends BlockComponent {

	public ModGhost() {
		super((component, defaultColour) -> Colour.BLACK.withAlpha(90),
				(component, defaultRadius) -> SolClientMod.instance.roundedUI ? 10F : 0F, (component, defaultOutlineWidth) -> defaultOutlineWidth);
	}

	@Override
	protected Rectangle getDefaultBounds() {
		return Rectangle.ofDimensions(300, 30);
	}

}
