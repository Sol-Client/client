package io.github.solclient.client.ui.component.impl;

import java.util.function.Consumer;

import io.github.solclient.client.mod.impl.SolClientConfig;
import io.github.solclient.client.platform.mc.DrawableHelper;
import io.github.solclient.client.platform.mc.Identifier;
import io.github.solclient.client.platform.mc.render.GlStateManager;
import io.github.solclient.client.ui.component.Component;
import io.github.solclient.client.ui.component.ComponentRenderInfo;
import io.github.solclient.client.ui.component.controller.AnimatedColourController;
import io.github.solclient.client.ui.component.controller.Controller;
import io.github.solclient.client.util.Utils;
import io.github.solclient.client.util.data.Colour;
import io.github.solclient.client.util.data.Rectangle;

public class SliderComponent extends Component {

	private float min;
	private float max;
	private float step;
	private float value;
	private final Consumer<Float> callback;
	private boolean selected;
	private final Controller<Colour> colour = new AnimatedColourController(
			(component, defaultColour) -> component.isHovered() || selected ? SolClientConfig.instance.uiHover : SolClientConfig.instance.uiColour);
	private final Component hoverController;

	public SliderComponent(float min, float max, float step, float value, Consumer<Float> callback, Component hoverController) {
		this.min = min;
		this.max = max;
		this.step = step;
		this.value = value;
		this.callback = callback;
		this.hoverController = hoverController;

		hoverController.onClick((info, button) -> {
			if(!super.isHovered()) {
				mouseClicked(info, button);
				return true;
			}

			return false;
		});
	}

	@Override
	public boolean isHovered() {
		return hoverController.isHovered();
	}

	@Override
	public void render(ComponentRenderInfo info) {
		GlStateManager.enableAlpha();
		GlStateManager.enableBlend();

		int x = (int) (100 * (((value - min) / (max - min)))) - 4;

		if(SolClientConfig.instance.roundedUI) {
			Colour.LIGHT_BUTTON.bind();
			mc.getTextureManager().bind(Identifier.minecraft(
					"textures/gui/sol_client_slider_" + Utils.getTextureScale() + ".png"));
			DrawableHelper.fillTexturedRect(0, 4, 0, 0, 100, 2, 100, 2);

			colour.get(this, null).bind();
			mc.getTextureManager().bind(Identifier.minecraft(
					"textures/gui/sol_client_slider_thumb_" + Utils.getTextureScale() + ".png"));
			DrawableHelper.fillTexturedRect(x, 1, 0, 0, 8, 8, 8, 8);
		}
		else {
			new Rectangle(0, 4, 100, 2).fill(Colour.LIGHT_BUTTON);
			new Rectangle(x, 1, 8, 8).fill(colour.get(this, null));
		}

		if(selected) {
			value = Utils.clamp((float) (min + Math.floor(((info.getRelativeMouseX() / 100F) * (max - min)) / step) * step), min, max);
			callback.accept(value);
		}

		super.render(info);
	}

	@Override
	public boolean mouseClicked(ComponentRenderInfo info, int button) {
		if(button == 0) {
			Utils.playClickSound(true);
			selected = true;
			return true;
		}

		return false;
	}

	@Override
	public boolean mouseReleasedAnywhere(ComponentRenderInfo info, int button, boolean inside) {
		if(selected) {
			selected = false;
			return true;
		}

		return false;
	}

	@Override
	protected Rectangle getDefaultBounds() {
		return new Rectangle(0, 0, 100, 10);
	}

}
