package me.mcblueparrot.client.ui.screen.mods;

import me.mcblueparrot.client.mod.Mod;
import me.mcblueparrot.client.mod.impl.SolClientMod;
import me.mcblueparrot.client.ui.component.ComponentRenderInfo;
import me.mcblueparrot.client.ui.component.controller.AlignedBoundsController;
import me.mcblueparrot.client.ui.component.controller.AnimatedController;
import me.mcblueparrot.client.ui.component.impl.ColouredComponent;
import me.mcblueparrot.client.ui.component.impl.ScaledIconComponent;
import me.mcblueparrot.client.util.Utils;
import me.mcblueparrot.client.util.data.Alignment;
import me.mcblueparrot.client.util.data.Colour;
import me.mcblueparrot.client.util.data.Rectangle;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class ModListing extends ColouredComponent {

	private Mod mod;

	public ModListing(Mod mod) {
		super(new AnimatedController<>((component,
				defaultColour) -> {
					if(mod.isEnabled()) {
						return component.isHovered() ? SolClientMod.instance.uiHover : SolClientMod.instance.uiColour;
					}
					else {
						return component.isHovered() ? Colour.DISABLED_MOD_HOVER : Colour.DISABLED_MOD;
					}
				}));
		this.mod = mod;

		add(new ScaledIconComponent("sol_client_" + mod.getId(), 16, 16),
				new AlignedBoundsController(Alignment.CENTRE, Alignment.CENTRE,
						(component, defaultBounds) -> new Rectangle(defaultBounds.getY(), defaultBounds.getY(),
								defaultBounds.getWidth(), defaultBounds.getHeight())));

		add(new ScaledIconComponent("sol_client_settings", 16, 16,
				new AnimatedController<>((component, defaultColour) -> isHovered()
						? (component.isHovered() ? Colour.LIGHT_BUTTON_HOVER : Colour.LIGHT_BUTTON)
						: Colour.TRANSPARENT)),
				new AlignedBoundsController(Alignment.CENTRE, Alignment.CENTRE,
						(component, defaultBounds) -> new Rectangle(getBounds().getWidth() - defaultBounds.getWidth() - defaultBounds.getY(),
								defaultBounds.getY(), defaultBounds.getWidth(), defaultBounds.getHeight())));
	}

	@Override
	public void render(ComponentRenderInfo info) {
		GlStateManager.enableAlpha();
		GlStateManager.enableBlend();

		Utils.glColour(Colour.BLACK_128);

		mc.getTextureManager().bindTexture(new ResourceLocation("textures/gui/sol_client_mod_listing_" + Utils.getTextureScale() + ".png"));
		Gui.drawModalRectWithCustomSizedTexture(0, 0, 0, 0, 300, 30, 300, 30);

		Utils.glColour(getColour());

		mc.getTextureManager().bindTexture(new ResourceLocation("textures/gui/sol_client_mod_listing_outline_" + Utils.getTextureScale() + ".png"));
		Gui.drawModalRectWithCustomSizedTexture(0, 0, 0, 0, 300, 30, 300, 30);

		font.renderString(mod.getName(), 30, 5, -1);
		font.renderString(mod.getDescription(), 30, 15, 0xFF999999);

		super.render(info);
	}

	@Override
	protected Rectangle getDefaultBounds() {
		return new Rectangle(0, 0, 300, 30);
	}

	@Override
	public boolean mouseClicked(ComponentRenderInfo info, int button) {
		if(button == 0) {
			Utils.playClickSound();
			mod.toggle();
		}

		return true;
	}

}
