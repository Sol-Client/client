package me.mcblueparrot.client.ui.component.impl;

import lombok.Getter;
import me.mcblueparrot.client.mod.impl.SolClientMod;
import me.mcblueparrot.client.ui.component.Component;
import me.mcblueparrot.client.ui.component.ComponentRenderInfo;
import me.mcblueparrot.client.ui.component.controller.AlignedBoundsController;
import me.mcblueparrot.client.ui.component.controller.AnimatedColourController;
import me.mcblueparrot.client.ui.component.controller.Controller;
import me.mcblueparrot.client.ui.component.handler.ClickHandler;
import me.mcblueparrot.client.util.Utils;
import me.mcblueparrot.client.util.data.Alignment;
import me.mcblueparrot.client.util.data.Colour;
import me.mcblueparrot.client.util.data.Rectangle;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

public class ButtonComponent extends ColouredComponent {

	@Getter
	private Controller<String> text;
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
		if(SolClientMod.instance.roundedUI) {
			GlStateManager.enableAlpha();
			GlStateManager.enableBlend();

			Utils.glColour(getColour());

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
