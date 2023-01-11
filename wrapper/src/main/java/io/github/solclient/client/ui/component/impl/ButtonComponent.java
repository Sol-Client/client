package io.github.solclient.client.ui.component.impl;

import org.lwjgl.nanovg.NanoVG;

import io.github.solclient.client.mod.impl.SolClientConfig;
import io.github.solclient.client.ui.component.ComponentRenderInfo;
import io.github.solclient.client.ui.component.controller.*;
import io.github.solclient.client.ui.component.handler.ClickHandler;
import io.github.solclient.client.util.Utils;
import io.github.solclient.client.util.data.*;
import lombok.Getter;
import net.minecraft.client.resources.I18n;

public class ButtonComponent extends ColouredComponent {

	@Getter
	private final Controller<String> text;
	private ButtonType type = ButtonType.NORMAL;

	public ButtonComponent(String text, Controller<Colour> colour) {
		this((component, defaultText) -> I18n.format(text), colour);
	}

	public ButtonComponent(Controller<String> text, Controller<Colour> colour) {
		super(colour);
		this.text = text;

		add(new LabelComponent(text), new AlignedBoundsController(Alignment.CENTRE, Alignment.CENTRE));
	}

	@Override
	public void render(ComponentRenderInfo info) {
		float radius = 0;

		if (SolClientConfig.instance.roundedUI)
			radius = 5;

		NanoVG.nvgBeginPath(nvg);
		NanoVG.nvgFillColor(nvg, getColour().withAlpha(140).nvg());
		NanoVG.nvgRoundedRect(nvg, 0, 0, type.getWidth(), 20, radius);
		NanoVG.nvgFill(nvg);

		NanoVG.nvgBeginPath(nvg);
		NanoVG.nvgStrokeColor(nvg, getColour().nvg());
		NanoVG.nvgStrokeWidth(nvg, 1);
		NanoVG.nvgRoundedRect(nvg, .5F, .5F, type.getWidth() - 1, 19, radius);
		NanoVG.nvgStroke(nvg);

		super.render(info);
	}

	@Override
	protected Rectangle getDefaultBounds() {
		return Rectangle.ofDimensions(type.getWidth(), 20);
	}

	@Override
	public ButtonComponent onClick(ClickHandler onClick) {
		super.onClick(onClick);
		return this;
	}

	public ButtonComponent withIcon(String name) {
		add(new ScaledIconComponent(name, 16, 16),
				new AlignedBoundsController(Alignment.START, Alignment.CENTRE,
						(component, defaultBounds) -> new Rectangle(defaultBounds.getY(), defaultBounds.getY(),
								defaultBounds.getWidth(), defaultBounds.getHeight())));
		return this;
	}

	public ButtonComponent type(ButtonType type) {
		this.type = type;
		return this;
	}

	public static ButtonComponent done(Runnable onClick) {
		return new ButtonComponent("gui.done", new AnimatedColourController(
				(component, defaultColour) -> component.isHovered() ? new Colour(20, 120, 20) : new Colour(0, 100, 0)))
				.onClick((info, button) -> {
					if (button == 0) {
						Utils.playClickSound(true);
						onClick.run();

						return true;
					}
					return false;
				}).withIcon("sol_client_tick");
	}

}
