package io.github.solclient.client.ui.screen.mods;

import java.net.URI;

import io.github.solclient.client.Client;
import io.github.solclient.client.mod.Mod;
import io.github.solclient.client.mod.impl.SolClientMod;
import io.github.solclient.client.ui.component.Component;
import io.github.solclient.client.ui.component.ComponentRenderInfo;
import io.github.solclient.client.ui.component.controller.AlignedBoundsController;
import io.github.solclient.client.ui.component.controller.AnimatedColourController;
import io.github.solclient.client.ui.component.impl.ColouredComponent;
import io.github.solclient.client.ui.component.impl.LabelComponent;
import io.github.solclient.client.ui.component.impl.ScaledIconComponent;
import io.github.solclient.client.ui.screen.mods.ModsScreen.ModsScreenComponent;
import io.github.solclient.client.util.Utils;
import io.github.solclient.client.util.data.Alignment;
import io.github.solclient.client.util.data.Colour;
import io.github.solclient.client.util.data.Rectangle;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class ModListing extends ColouredComponent {

	private Mod mod;
	private ModsScreenComponent screen;
	private Component settingsButton;

	public ModListing(Mod mod, ModsScreenComponent screen) {
		super(new AnimatedColourController((component,
				defaultColour) -> {
					if(mod.isEnabled()) {
						return component.isHovered() ? SolClientMod.instance.uiHover : SolClientMod.instance.uiColour;
					}
					else if(mod.isBlocked()) {
						return component.isHovered() ? Colour.RED_HOVER : Colour.PURE_RED;
					}
					else {
						return component.isHovered() ? Colour.DISABLED_MOD_HOVER : Colour.DISABLED_MOD;
					}
				}));

		this.mod = mod;
		this.screen = screen;

		add(new ScaledIconComponent("sol_client_" + mod.getId(), 16, 16),
				new AlignedBoundsController(Alignment.CENTRE, Alignment.CENTRE,
						(component, defaultBounds) -> new Rectangle(defaultBounds.getY(), defaultBounds.getY(),
								defaultBounds.getWidth(), defaultBounds.getHeight())));

		add(settingsButton = new ScaledIconComponent((component, defaultIcon) -> mod.isBlocked() ? "sol_client_lock" : "sol_client_settings", 16, 16,
				new AnimatedColourController((component, defaultColour) -> isHovered()
						? (component.isHovered() || mod.isLocked() || mod.isBlocked() ? Colour.LIGHT_BUTTON_HOVER : Colour.LIGHT_BUTTON)
						: Colour.TRANSPARENT)),
				new AlignedBoundsController(Alignment.CENTRE, Alignment.CENTRE,
						(component, defaultBounds) -> new Rectangle(getBounds().getWidth() - defaultBounds.getWidth() - defaultBounds.getY(),
								defaultBounds.getY(), defaultBounds.getWidth(), defaultBounds.getHeight())));

		add(new LabelComponent((component, defaultText) -> mod.getName() + (mod.isBlocked() ? " (blocked)" : "")),
				new AlignedBoundsController(Alignment.START, Alignment.CENTRE,
						(component, defaultBounds) -> new Rectangle(defaultBounds.getX() + 30,
								defaultBounds.getY() - (font.getHeight() / 2) - (SolClientMod.instance.fancyFont ? 0 : 1), defaultBounds.getWidth(), defaultBounds.getHeight())));
		add(new LabelComponent((component, defaultText) -> mod.getDescription(),
				(component, defaultColour) -> new Colour(160, 160, 160)), new AlignedBoundsController(Alignment.START, Alignment.CENTRE, (component, defaultBounds) -> new Rectangle(defaultBounds.getX() + 30,
						defaultBounds.getY() + (font.getHeight() / 2) + (SolClientMod.instance.fancyFont ? 0 : 1), defaultBounds.getWidth(), defaultBounds.getHeight())));
	}

	@Override
	public void render(ComponentRenderInfo info) {
		GlStateManager.enableAlpha();
		GlStateManager.enableBlend();

		if(SolClientMod.instance.roundedUI) {
			Colour.BLACK_128.bind();
			mc.getTextureManager().bindTexture(new ResourceLocation("textures/gui/sol_client_mod_listing_" + Utils.getTextureScale() + ".png"));
			Gui.drawModalRectWithCustomSizedTexture(0, 0, 0, 0, 300, 30, 300, 30);

			getColour().bind();
			mc.getTextureManager().bindTexture(new ResourceLocation("textures/gui/sol_client_mod_listing_outline_" + Utils.getTextureScale() + ".png"));
			Gui.drawModalRectWithCustomSizedTexture(0, 0, 0, 0, 300, 30, 300, 30);
		}
		else {

			Utils.drawRectangle(getRelativeBounds(), Colour.BLACK_128);
			Utils.drawOutline(getRelativeBounds(), getColour());
		}

		super.render(info);
	}

	@Override
	protected Rectangle getDefaultBounds() {
		return new Rectangle(0, 0, 300, 30);
	}

	@Override
	public boolean mouseClicked(ComponentRenderInfo info, int button) {
		if(button == 0 || (!mod.isBlocked() && (button == 0 || button == 1))) {
			if(mod.isBlocked()) {
				if(Client.INSTANCE.detectedServer == null) {
					return false;
				}

				URI blockedModPage = Client.INSTANCE.detectedServer.getBlockedModPage();

				if(blockedModPage != null) {
					Utils.openUrl(blockedModPage.toString());
				}

				return false;
			}

			Utils.playClickSound(true);

			if(settingsButton.isHovered() || mod.isLocked() || button == 1) {
				screen.switchMod(mod);
				return true;
			}

			mod.toggle();

			return true;
		}

		return false;
	}

}
