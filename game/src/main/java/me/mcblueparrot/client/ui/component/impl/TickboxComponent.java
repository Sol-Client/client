package me.mcblueparrot.client.ui.component.impl;

import java.util.function.Consumer;

import me.mcblueparrot.client.mod.impl.SolClientMod;
import me.mcblueparrot.client.ui.component.Component;
import me.mcblueparrot.client.ui.component.ComponentRenderInfo;
import me.mcblueparrot.client.ui.component.controller.AnimatedColourController;
import me.mcblueparrot.client.util.Utils;
import me.mcblueparrot.client.util.data.Colour;

public class TickboxComponent extends ScaledIconComponent {

	private boolean value;
	private Consumer<Boolean> booleanConsumer;
	private Component hoverController;

	public TickboxComponent(boolean value, Consumer<Boolean> booleanConsumer, Component hoverController) {
		super("sol_client_tickbox", 16, 16,
				new AnimatedColourController((component, defaultColour) -> component.isHovered() ? SolClientMod.instance.uiHover
						: SolClientMod.instance.uiColour));

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
						? (isHovered() ? SolClientMod.instance.uiHover : SolClientMod.instance.uiColour)
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
		Utils.drawOutline(getRelativeBounds(), getColour());
	}

}
