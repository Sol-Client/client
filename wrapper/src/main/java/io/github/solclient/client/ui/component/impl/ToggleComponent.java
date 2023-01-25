package io.github.solclient.client.ui.component.impl;

import java.util.function.Consumer;

import org.lwjgl.nanovg.NanoVG;

import io.github.solclient.client.ui.component.*;
import io.github.solclient.client.ui.component.controller.*;
import io.github.solclient.client.util.MinecraftUtils;
import io.github.solclient.client.util.data.*;

public class ToggleComponent extends BlockComponent {

	private boolean value;
	private final AnimatedFloatController handleX;
	private final Controller<Colour> handleColour;
	private final Consumer<Boolean> booleanConsumer;

	public ToggleComponent(boolean value, Consumer<Boolean> booleanConsumer) {
		super(new AnimatedColourController((component, defaultColour) -> {
			if (((ToggleComponent) component).value)
				return component.isHovered() ? theme.accentHover : theme.accent;

			return component.isHovered() ? theme.buttonHover : theme.button;
		}), (component, defaultRadius) -> component.getBounds().getHeight() / 2F,
				(component, defaultStrokeWidth) -> 0F);

		this.value = value;
		this.booleanConsumer = booleanConsumer;

		handleX = new AnimatedFloatController((component, ignored) -> {
			float x = getBounds().getHeight() / 2F;
			if (this.value)
				x = getBounds().getWidth() - x;
			return x;
		}, 200);
		handleColour = new AnimatedColourController(
				(component, defaultValue) -> this.value ? theme.accentFg : theme.fg, 300);
	}

	@Override
	public void render(ComponentRenderInfo info) {
		super.render(info);

		NanoVG.nvgBeginPath(nvg);
		NanoVG.nvgCircle(nvg, handleX.get(this), getBounds().getHeight() / 2F, 4);
		NanoVG.nvgFillColor(nvg, handleColour.get(this).nvg());
		NanoVG.nvgFill(nvg);
	}

	@Override
	public boolean mouseClicked(ComponentRenderInfo info, int button) {
		if (button != 0)
			return false;

		MinecraftUtils.playClickSound(true);
		value = !value;
		booleanConsumer.accept(value);

		return true;
	}

	@Override
	protected Rectangle getDefaultBounds() {
		return Rectangle.ofDimensions(24, 11);
	}

}
