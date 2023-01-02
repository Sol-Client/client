package io.github.solclient.client.ui.component.impl;

import org.lwjgl.nanovg.NanoVG;

import io.github.solclient.client.ui.component.ComponentRenderInfo;
import io.github.solclient.client.ui.component.controller.Controller;
import io.github.solclient.client.util.data.*;
import net.minecraft.client.resources.I18n;

public class LabelComponent extends ColouredComponent {

	private final Controller<String> text;

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
		NanoVG.nvgFillColor(nvg, getColour().nvg());
		regularFont.renderString(nvg, getText(), 0, 0);

		super.render(info);
	}

	@Override
	protected Rectangle getDefaultBounds() {
		return new Rectangle(0, 0, (int) regularFont.getWidth(nvg, getText()), (int) regularFont.getHeight());
	}

	public String getText() {
		return text.get(this, "");
	}

}
