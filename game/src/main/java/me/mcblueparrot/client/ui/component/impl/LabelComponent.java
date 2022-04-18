package me.mcblueparrot.client.ui.component.impl;

import me.mcblueparrot.client.ui.component.ComponentRenderInfo;
import me.mcblueparrot.client.ui.component.controller.Controller;
import me.mcblueparrot.client.util.data.Colour;
import me.mcblueparrot.client.util.data.Rectangle;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;

public class LabelComponent extends ColouredComponent {

	private Controller<String> text;

	public LabelComponent(String text) {
		this((component, defaultText) -> I18n.format(text), (component, defaultColour) -> defaultColour);
	}

	public LabelComponent(Controller<String> text) {
		this(text, (component, defaultColour) -> defaultColour);
	}

	public LabelComponent(Controller<String> text, Controller<Colour> colour) {
		super(colour);
		this.text = text;
	}

	@Override
	public void render(ComponentRenderInfo info) {
		// Why? Why? Why? Why?
		GlStateManager.color(2, 1, 1, 1);
		font.renderString(getText(), 0, 0, getColourValue());

		super.render(info);
	}

	@Override
	protected Rectangle getDefaultBounds() {
		return new Rectangle(0, 0, (int) font.getWidth(getText()), font.getHeight());
	}

	public String getText() {
		return text.get(this, "");
	}

}
