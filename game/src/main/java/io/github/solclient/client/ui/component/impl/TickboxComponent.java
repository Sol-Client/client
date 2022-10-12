package io.github.solclient.client.ui.component.impl;

import java.util.function.Consumer;

import io.github.solclient.client.mod.impl.SolClientConfig;
import io.github.solclient.client.ui.component.*;
import io.github.solclient.client.ui.component.controller.AnimatedColourController;
import io.github.solclient.client.util.Utils;
import io.github.solclient.client.util.data.Colour;

public class TickboxComponent extends ScaledIconComponent {

	private boolean value;
	private final Consumer<Boolean> booleanConsumer;
	private final Component hoverController;

	public TickboxComponent(boolean value, Consumer<Boolean> booleanConsumer, Component hoverController) {
		super("sol_client_tickbox", 16, 16,
				new AnimatedColourController((component, defaultColour) -> component.isHovered() ? SolClientConfig.INSTANCE.uiHover
						: SolClientConfig.INSTANCE.uiColour));

		this.value = value;
		this.booleanConsumer = booleanConsumer;
		this.hoverController = hoverController;

		hoverController.onClick((info, button) -> {
			if(!super.isHovered() && button == 0) {
				mouseClicked(info, button);
			}

			return true;
		});

		add(new ScaledIconComponent("sol_client_small_tick", 16, 16,
				new AnimatedColourController((component, defaultColour) -> this.value
						? (isHovered() ? SolClientConfig.INSTANCE.uiHover : SolClientConfig.INSTANCE.uiColour)
						: Colour.TRANSPARENT)),
				(component, defaultBounds) -> defaultBounds);
	}

	@Override
	public boolean isHovered() {
		return hoverController.isHovered();
	}

	@Override
	public boolean mouseClicked(ComponentRenderInfo info, int button) {
		if(button != 0) {
			return false;
		}

		Utils.playClickSound(true);
		value = !value;
		booleanConsumer.accept(value);

		return true;
	}

	@Override
	public boolean useFallback() {
		return true;
	}

	@Override
	public void renderFallback(ComponentRenderInfo info) {
		getRelativeBounds().stroke(getColour());
	}

}
