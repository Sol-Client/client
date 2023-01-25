package io.github.solclient.client.ui.component.impl;

import org.lwjgl.nanovg.NanoVG;

import io.github.solclient.client.mod.impl.SolClientConfig;
import io.github.solclient.client.ui.component.ComponentRenderInfo;
import io.github.solclient.client.ui.component.controller.*;
import io.github.solclient.client.ui.component.handler.ClickHandler;
import io.github.solclient.client.util.MinecraftUtils;
import io.github.solclient.client.util.data.*;
import lombok.Getter;
import net.minecraft.client.resource.language.I18n;

public class ButtonComponent extends ColouredComponent {

	private boolean icon;
	private int width = 100;
	@Getter
	private final Controller<String> text;
	private final Controller<Colour> fg;

	public ButtonComponent(String text, Controller<Colour> colour, Controller<Colour> fg) {
		this((component, defaultText) -> I18n.translate(text), colour, fg);
	}

	public ButtonComponent(Controller<String> text, Controller<Colour> colour, Controller<Colour> fg) {
		super(colour);
		this.text = text;
		this.fg = fg;

		add(new LabelComponent(text, fg), new AlignedBoundsController(Alignment.CENTRE, Alignment.CENTRE, (component, defaultBounds) -> {
			if (!icon)
				return defaultBounds;

			return defaultBounds.offset(5, 0);
		}));
	}

	@Override
	public void render(ComponentRenderInfo info) {
		float radius = 0;

		if (SolClientConfig.instance.roundedUI)
			radius = getBounds().getHeight() / 2;

		NanoVG.nvgBeginPath(nvg);
		NanoVG.nvgFillColor(nvg, getColour().nvg());
		NanoVG.nvgRoundedRect(nvg, 0, 0, width, 20, radius);
		NanoVG.nvgFill(nvg);

		super.render(info);
	}

	@Override
	protected Rectangle getDefaultBounds() {
		return Rectangle.ofDimensions(width, 20);
	}

	@Override
	public ButtonComponent onClick(ClickHandler onClick) {
		super.onClick(onClick);
		return this;
	}

	public ButtonComponent withIcon(String name) {
		icon = true;
		add(new ScaledIconComponent(name, 16, 16, fg),
				new AlignedBoundsController(Alignment.START, Alignment.CENTRE,
						(component, defaultBounds) -> new Rectangle(defaultBounds.getY(), defaultBounds.getY(),
								defaultBounds.getWidth(), defaultBounds.getHeight())));
		return this;
	}

	public ButtonComponent width(int width) {
		this.width = width;
		return this;
	}

	public static ButtonComponent done(Runnable onClick) {
		return new ButtonComponent("gui.done", theme.accent(), Controller.of(theme.accentFg))
				.onClick((info, button) -> {
					if (button == 0) {
						MinecraftUtils.playClickSound(true);
						onClick.run();

						return true;
					}
					return false;
				}).withIcon("sol_client_tick");
	}

}
