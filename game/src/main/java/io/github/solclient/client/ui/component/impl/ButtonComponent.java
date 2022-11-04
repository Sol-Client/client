package io.github.solclient.client.ui.component.impl;

import io.github.solclient.client.mod.impl.SolClientConfig;
import io.github.solclient.client.platform.mc.DrawableHelper;
import io.github.solclient.client.platform.mc.lang.I18n;
import io.github.solclient.client.platform.mc.render.GlStateManager;
import io.github.solclient.client.platform.mc.resource.Identifier;
import io.github.solclient.client.ui.component.ComponentRenderInfo;
import io.github.solclient.client.ui.component.controller.*;
import io.github.solclient.client.ui.component.handler.ClickHandler;
import io.github.solclient.client.util.Utils;
import io.github.solclient.client.util.data.*;
import lombok.Getter;

public final class ButtonComponent extends ColouredComponent {

	@Getter
	private final Controller<String> text;
	private ButtonType type = ButtonType.NORMAL;

	public ButtonComponent(String text, Controller<Colour> colour) {
		this((component, defaultText) -> I18n.translate(text), colour);
	}

	public ButtonComponent(Controller<String> text, Controller<Colour> colour) {
		super(colour);
		this.text = text;

		add(new LabelComponent(text), new AlignedBoundsController(Alignment.CENTRE, Alignment.CENTRE));
	}

	@Override
	public void render(ComponentRenderInfo info) {
		if(SolClientConfig.INSTANCE.roundedUI) {
			GlStateManager.enableBlend();

			getColour().bind();

			mc.getTextureManager().bind(Identifier
					.minecraft("textures/gui/sol_client_button_" + type + "_" + Utils.getTextureScale() + ".png"));
			DrawableHelper.fillTexturedRect(0, 0, 0, 0, type.getWidth(), 20, type.getWidth(), 20);
		}
		else {
			Colour colour = getColour().withAlpha(140);
			getRelativeBounds().fill(colour);
			getRelativeBounds().stroke(colour);
		}

		super.render(info);
	}

	@Override
	protected Rectangle getDefaultBounds() {
		return new Rectangle(0, 0, type.getWidth(), 20);
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
							if(button == 0) {
								Utils.playClickSound(true);
								onClick.run();

								return true;
							}
							return false;
						}).withIcon("sol_client_tick");
	}

}
