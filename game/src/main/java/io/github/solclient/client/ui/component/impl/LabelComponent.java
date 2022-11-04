package io.github.solclient.client.ui.component.impl;

import io.github.solclient.client.platform.mc.lang.I18n;
import io.github.solclient.client.ui.component.ComponentRenderInfo;
import io.github.solclient.client.ui.component.controller.Controller;
import io.github.solclient.client.util.data.*;

public final class LabelComponent extends ColouredComponent {

	private final Controller<String> text;

	public LabelComponent(String text) {
		this((component, defaultText) -> I18n.translate(text), (component, defaultColour) -> defaultColour);
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
		font.render(getText(), 0, 0, getColourValue());

		super.render(info);
	}

	@Override
	protected Rectangle getDefaultBounds() {
		return new Rectangle(0, 0, (int) font.getTextWidth(getText()), font.getHeight());
	}

	public String getText() {
		return text.get(this, "");
	}

}
