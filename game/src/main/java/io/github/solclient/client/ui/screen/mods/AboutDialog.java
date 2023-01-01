package io.github.solclient.client.ui.screen.mods;

import io.github.solclient.client.GlobalConstants;
import io.github.solclient.client.ui.component.ComponentRenderInfo;
import io.github.solclient.client.ui.component.controller.AlignedBoundsController;
import io.github.solclient.client.ui.component.impl.*;
import io.github.solclient.client.util.Utils;
import io.github.solclient.client.util.data.*;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class AboutDialog extends ScaledIconComponent {

	public AboutDialog() {
		super("sol_client_about_dialog", 200, 200, (component, defaultColour) -> new Colour(40, 40, 40));

		add(new LabelComponent("sol_client.about"),
				new AlignedBoundsController(Alignment.CENTRE, Alignment.START,
						(component, defaultBounds) -> new Rectangle(defaultBounds.getX(), defaultBounds.getY() + 9,
								defaultBounds.getWidth(), defaultBounds.getHeight())));

		add(ButtonComponent.done(() -> parent.setDialog(null)),
				new AlignedBoundsController(Alignment.CENTRE, Alignment.END,
						(component, defaultBounds) -> new Rectangle(defaultBounds.getX(), defaultBounds.getY() - 8,
								defaultBounds.getWidth(), defaultBounds.getHeight())));
	}

	@Override
	public boolean useFallback() {
		return true;
	}

	@Override
	public void renderFallback(ComponentRenderInfo info) {
		Utils.drawRectangle(getRelativeBounds(), getColour());
	}

	@Override
	public void render(ComponentRenderInfo info) {
		super.render(info);
		GlStateManager.color(1, 1, 1, 1);
		mc.getTextureManager().bindTexture(new ResourceLocation("textures/gui/sol_client_icon.png"));
		Gui.drawModalRectWithCustomSizedTexture(getBounds().getWidth() / 2 - 32, getBounds().getHeight() / 2 - 60, 0, 0,
				64, 64, 64, 64);

		String versionString = "Sol Client version " + GlobalConstants.VERSION_STRING;
		font.renderString(versionString, getBounds().getWidth() / 2 - font.getWidth(versionString) / 2, 110, -1);
		String copyrightString = "Copyright © 2022 TheKodeToad and contributors.";
		font.renderString(copyrightString, getBounds().getWidth() / 2 - font.getWidth(copyrightString) / 2, 125, -1);
		String licenseString = "Released under the GPL v3 license.";
		font.renderString(licenseString, getBounds().getWidth() / 2 - font.getWidth(licenseString) / 2, 140, -1);
	}

}
