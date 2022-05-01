package me.mcblueparrot.client.ui.component.impl;

import java.util.function.Consumer;

import me.mcblueparrot.client.mod.impl.SolClientMod;
import me.mcblueparrot.client.ui.component.Component;
import me.mcblueparrot.client.ui.component.ComponentRenderInfo;
import me.mcblueparrot.client.ui.component.controller.AnimatedColourController;
import me.mcblueparrot.client.ui.component.controller.Controller;
import me.mcblueparrot.client.util.Utils;
import me.mcblueparrot.client.util.data.Colour;
import me.mcblueparrot.client.util.data.Rectangle;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class SliderComponent extends Component {

	private float min;
	private float max;
	private float step;
	private float value;
	private final Consumer<Float> callback;
	private boolean selected;
	private final Controller<Colour> colour = new AnimatedColourController(
			(component, defaultColour) -> component.isHovered() || selected ? SolClientMod.instance.uiHover : SolClientMod.instance.uiColour);
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

		if(SolClientMod.instance.roundedUI) {
			Utils.glColour(Colour.LIGHT_BUTTON);
			mc.getTextureManager().bindTexture(new ResourceLocation(
					"textures/gui/sol_client_slider_" + Utils.getTextureScale() + ".png"));
			Gui.drawModalRectWithCustomSizedTexture(0, 4, 0, 0, 100, 2, 100, 2);

			Utils.glColour(colour.get(this, null));
			mc.getTextureManager().bindTexture(new ResourceLocation(
					"textures/gui/sol_client_slider_thumb_" + Utils.getTextureScale() + ".png"));
			Gui.drawModalRectWithCustomSizedTexture(x, 1, 0, 0, 8, 8, 8, 8);
		}
		else {
			Utils.drawRectangle(new Rectangle(0, 4, 100, 2), Colour.LIGHT_BUTTON);
			Utils.drawRectangle(new Rectangle(x, 1, 8, 8), colour.get(this, null));
		}

		if(selected) {
			value = MathHelper.clamp_float((float) (min + Math.floor(((info.getRelativeMouseX() / 100F) * (max - min)) / step) * step), min, max);
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
