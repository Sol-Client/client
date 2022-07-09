package io.github.solclient.client.ui.component.impl;

import org.lwjgl.opengl.GL11;

import io.github.solclient.client.mod.impl.SolClientConfig;
import io.github.solclient.client.ui.component.Component;
import io.github.solclient.client.ui.component.ComponentRenderInfo;
import io.github.solclient.client.ui.component.controller.AlignedBoundsController;
import io.github.solclient.client.ui.component.controller.AnimatedColourController;
import io.github.solclient.client.ui.component.controller.Controller;
import io.github.solclient.client.ui.component.handler.ClickHandler;
import io.github.solclient.client.util.Utils;
import io.github.solclient.client.util.data.Alignment;
import io.github.solclient.client.util.data.Colour;
import io.github.solclient.client.util.data.Rectangle;
import lombok.Getter;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

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
		if(SolClientConfig.instance.roundedUI) {
			GL11.glEnable(GL11.GL_ALPHA);
			GL11.glEnable(GL11.GL_BLEND);

			getColour().bind();

			mc.getTextureManager().bindTexture(
					new ResourceLocation("textures/gui/sol_client_button_" + type + "_" + Utils.getTextureScale() + ".png"));
			Gui.drawModalRectWithCustomSizedTexture(0, 0, 0, 0, type.getWidth(), 20, type.getWidth(), 20);
		}
		else {
			Utils.drawRectangle(getRelativeBounds(), getColour().withAlpha(140));
			Utils.drawOutline(getRelativeBounds(), getColour().withAlpha(140));
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
							if (button == 0) {
								Utils.playClickSound(true);
								onClick.run();

								return true;
							}
							return false;
						}).withIcon("sol_client_tick");
	}

}
