package io.github.solclient.client.ui.screen.mods;

import io.github.solclient.client.ui.component.ComponentRenderInfo;
import io.github.solclient.client.ui.component.impl.ScaledIconComponent;
import io.github.solclient.client.util.Utils;
import io.github.solclient.client.util.data.Colour;
import net.minecraft.client.renderer.GlStateManager;

public class ModGhost extends ScaledIconComponent {

	public ModGhost() {
		super("sol_client_mod_listing", 300, 30, (component, defaultColour) -> new Colour(0, 0, 0, 90));
	}

	@Override
	public boolean useFallback() {
		return true;
	}

	@Override
	public void renderFallback(ComponentRenderInfo info) {
		GlStateManager.enableAlpha();
		GlStateManager.enableBlend();
		getColour().bind();
		Utils.drawRectangle(getRelativeBounds().offset(1, 1).grow(-2, -2), getColour());
	}

}
