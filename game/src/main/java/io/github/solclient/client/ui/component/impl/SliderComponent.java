package io.github.solclient.client.ui.component.impl;

import java.util.function.Consumer;

import org.lwjgl.nanovg.NanoVG;

import io.github.solclient.client.mod.impl.SolClientMod;
import io.github.solclient.client.ui.component.*;
import io.github.solclient.client.ui.component.controller.*;
import io.github.solclient.client.util.Utils;
import io.github.solclient.client.util.data.*;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.*;

public class SliderComponent extends Component {

	private float min;
	private float max;
	private float step;
	private float value;
	private final Consumer<Float> callback;
	private boolean selected;
	private final Controller<Colour> colour = new AnimatedColourController(
			(component, defaultColour) -> component.isHovered() || selected ? SolClientMod.instance.uiHover
					: SolClientMod.instance.uiColour);
	private final Component hoverController;

	public SliderComponent(float min, float max, float step, float value, Consumer<Float> callback,
			Component hoverController) {
		this.min = min;
		this.max = max;
		this.step = step;
		this.value = value;
		this.callback = callback;
		this.hoverController = hoverController;

		hoverController.onClick((info, button) -> {
			if (super.isHovered()) {
				return false;
			}

			mouseClicked(info, button);
			return true;
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

		int x = (int) (100 * (((value - min) / (max - min))));

		NanoVG.nvgBeginPath(nvg);
		NanoVG.nvgFillColor(nvg, Colour.LIGHT_BUTTON.nvg());
		NanoVG.nvgRoundedRect(nvg, 0, 4, 100, 2, SolClientMod.instance.roundedUI ? 1 : 0);
		NanoVG.nvgFill(nvg);

		NanoVG.nvgFillColor(nvg, colour.get(this, null).nvg());

		if (SolClientMod.instance.roundedUI) {
			NanoVG.nvgBeginPath(nvg);
			NanoVG.nvgCircle(nvg, x, 5, 4);
			NanoVG.nvgFill(nvg);
		} else {
			NanoVG.nvgBeginPath(nvg);
			NanoVG.nvgRect(nvg, x - 2, 1, 4, 8);
			NanoVG.nvgFill(nvg);
		}

		if (selected) {
			value = MathHelper.clamp_float(
					(float) (min + Math.floor(((info.getRelativeMouseX() / 100F) * (max - min)) / step) * step), min,
					max);
			callback.accept(value);
		}

		super.render(info);
	}

	@Override
	public boolean mouseClicked(ComponentRenderInfo info, int button) {
		if (button == 0) {
			Utils.playClickSound(true);
			selected = true;
			return true;
		}

		return false;
	}

	@Override
	public boolean mouseReleasedAnywhere(ComponentRenderInfo info, int button, boolean inside) {
		if (selected) {
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
