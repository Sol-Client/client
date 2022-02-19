package me.mcblueparrot.client.ui.component.impl;

import me.mcblueparrot.client.ui.component.Component;
import me.mcblueparrot.client.ui.component.ComponentRenderInfo;
import me.mcblueparrot.client.ui.component.controller.AlignedBoundsController;
import me.mcblueparrot.client.ui.component.controller.Controller;
import me.mcblueparrot.client.ui.component.handler.ClickHandler;
import me.mcblueparrot.client.util.Utils;
import me.mcblueparrot.client.util.data.Alignment;
import me.mcblueparrot.client.util.data.Colour;
import me.mcblueparrot.client.util.data.Rectangle;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class ButtonComponent extends ColouredComponent {

	public ButtonComponent(String text, Controller<Colour> colour) {
		this((component, defaultText) -> text, colour);
	}

	public ButtonComponent(Controller<String> text, Controller<Colour> colour) {
		super(colour);
		add(new LabelComponent(text), new AlignedBoundsController(Alignment.CENTRE, Alignment.CENTRE));
	}

	@Override
	public void render(ComponentRenderInfo info) {
		GlStateManager.enableAlpha();
		GlStateManager.enableBlend();

		Utils.glColour(getColour());

		mc.getTextureManager().bindTexture(
				new ResourceLocation("textures/gui/sol_client_button_" + Utils.getTextureScale() + ".png"));
		Gui.drawModalRectWithCustomSizedTexture(0, 0, 0, 0, 100, 20, 100, 20);

		super.render(info);
	}

	@Override
	protected Rectangle getDefaultBounds() {
		return new Rectangle(0, 0, 100, 20);
	}

	@Override
	public ButtonComponent onClick(ClickHandler onClick) {
		super.onClick(onClick);

		return this;
	}

	public Component withIcon(String name) {
		add(new ScaledIconComponent("sol_client_tick", 16, 16),
				new AlignedBoundsController(Alignment.START, Alignment.CENTRE,
						(component, defaultBounds) -> new Rectangle(defaultBounds.getY(), defaultBounds.getY(),
								defaultBounds.getWidth(), defaultBounds.getHeight())));
		return this;
	}

}
