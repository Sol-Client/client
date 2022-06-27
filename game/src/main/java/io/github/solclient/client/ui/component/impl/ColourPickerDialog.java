package io.github.solclient.client.ui.component.impl;

import java.awt.Color;
import java.util.function.Consumer;

import io.github.solclient.client.Client;
import io.github.solclient.client.mod.Mod;
import io.github.solclient.client.mod.ModOption;
import io.github.solclient.client.mod.impl.SolClientMod;
import io.github.solclient.client.ui.component.ComponentRenderInfo;
import io.github.solclient.client.ui.component.controller.AlignedBoundsController;
import io.github.solclient.client.ui.component.controller.AnimatedColourController;
import io.github.solclient.client.util.Utils;
import io.github.solclient.client.util.data.Alignment;
import io.github.solclient.client.util.data.Colour;
import io.github.solclient.client.util.data.Rectangle;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.MathHelper;

public class ColourPickerDialog extends ScaledIconComponent {

	private Colour colour;
	private final Consumer<Colour> callback;
	private int selectedSlider = -1;

	private static final int RGB_OFFSET_TOP = 24;
	private static final int RGB_OFFSET_LEFT = 22;
	private static final int RGB_SPACING = 20;

	private final TextFieldComponent hex;
	private final ButtonComponent done;

	public ColourPickerDialog(ModOption colourOption, Colour colour, Consumer<Colour> callback) {
		super("sol_client_colour_dialog", 300, 150, (component, defaultColour) -> new Colour(40, 40, 40));
		add(hex = new TextFieldComponent(60, true), new AlignedBoundsController(Alignment.CENTRE, Alignment.END, (component, defaultBounds) -> new Rectangle(defaultBounds.getX(), defaultBounds.getY() - 32, defaultBounds.getWidth(), defaultBounds.getHeight())));

		add(new LabelComponent(colourOption.getName()),
				new AlignedBoundsController(Alignment.CENTRE, Alignment.START,
						(component, defaultBounds) -> new Rectangle(defaultBounds.getX(), defaultBounds.getY() + 9,
								defaultBounds.getWidth(), defaultBounds.getHeight())));

		add(done = ButtonComponent.done(() -> {
			hex.flush();
			parent.setDialog(null);
		}), new AlignedBoundsController(Alignment.CENTRE, Alignment.END,
				(component, defaultBounds) -> new Rectangle(defaultBounds.getX() - (colourOption.canApplyToAll() ? 50 : 0), defaultBounds.getY() - 5,
						defaultBounds.getWidth(), defaultBounds.getHeight())));

		if(colourOption.canApplyToAll()) {
			add(new ButtonComponent("sol_client.mod.screen.apply_to_all",
					new AnimatedColourController(
							(component, defaultColour) -> component.isHovered() ? Colour.BLUE_HOVER : Colour.BLUE))
									.withIcon("sol_client_new")
									.onClick((info, button) -> {
										if(button == 0) {
											hex.flush();
											parent.setDialog(null);
											Utils.playClickSound(true);
											colourOption.applyToAll();
											return true;
										}

										return false;
									}),
					new AlignedBoundsController(Alignment.CENTRE, Alignment.END,
							(component, defaultBounds) -> new Rectangle(defaultBounds.getX() + 50, defaultBounds.getY() - 5,
									defaultBounds.getWidth(), defaultBounds.getHeight())));
		}

		add(new ColourBoxComponent((component, defaultColour) -> this.colour, null), (component, defaultBounds) -> new Rectangle(done.getBounds().getX() - 20, done.getBounds().getY() + 2, defaultBounds.getWidth(), defaultBounds.getHeight()));

		this.colour = colour;
		this.callback = callback;

		updateHex();
		hex.onUpdate((text) -> {
			Colour parsed = Colour.fromHexString(font);

			if(parsed != null) {
				this.colour = parsed;
				callback.accept(this.colour);
				updateHex();
			}

			return parsed != null;
		});
	}

	private void updateHex() {
		hex.setText(colour.toHexString());
	}

	@Override
	public boolean useFallback() {
		return true;
	}

	@Override
	public void renderFallback(ComponentRenderInfo info) {
		Utils.drawRectangle(getRelativeBounds(), getColour());
	}

	@Override
	public void render(ComponentRenderInfo info) {
		super.render(info);

		if(selectedSlider != -1) {
			colour = colour.withComponent(selectedSlider, MathHelper.clamp_int(info.getRelativeMouseX() - RGB_OFFSET_LEFT, 0, 255));
			callback.accept(colour);
			updateHex();
		}

		for(int component = 0; component < 4; component++) {
			Rectangle rectangle = new Rectangle(RGB_OFFSET_LEFT, RGB_OFFSET_TOP + component * RGB_SPACING + 1, 256, 10);

			if(component == 3) {
				for(int x = 0; x < 250; x += 10) {
					new Rectangle(rectangle.getX() + x, rectangle.getY(), 5, 5).fill(new Colour(70, 70, 70));
					new Rectangle(rectangle.getX() + x + 5, rectangle.getY() + 5, 5, 5).fill(new Colour(70, 70, 70));
				}

				for(int x = 0; x < 250; x += 10) {
					new Rectangle(rectangle.getX() + x + 5, rectangle.getY(), 5, 5).fill(new Colour(50, 50, 50));
					new Rectangle(rectangle.getX() + x, rectangle.getY() + 5, 5, 5).fill(new Colour(50, 50, 50));
				}
			}

			String name = "?";
			switch(component) {
				case 0:
					name = "R";
					break;
				case 1:
					name = "G";
					break;
				case 2:
					name = "B";
					break;
				case 3:
					name = "A";
					break;
			}

			font.renderString(name, rectangle.getX() - 10, rectangle.getY() + 5 - (font.getHeight() / 2), -1);

			for(int i = 0; i < 256; i++) {
				Colour stripColour = Colour.BLACK.withComponent(component, i);

				if(colour.getComponents()[component] == i) {
					stripColour = Colour.WHITE;

					font.renderString(Integer.toString(i), RGB_OFFSET_LEFT + i + 1 - (font.getWidth(Integer.toString(i)) / 2), rectangle.getY() + (SolClientMod.instance.fancyFont ? 9 : 11), -1);
				}

				Utils.renderVerticalLine(RGB_OFFSET_LEFT + i, RGB_OFFSET_TOP + component * RGB_SPACING, RGB_OFFSET_TOP + (component * RGB_SPACING) + 11, stripColour.getValue());
			}
		}
	}

	@Override
	public boolean mouseClicked(ComponentRenderInfo info, int button) {
		int selected = getSelectedRGBComponent(info);

		if(button == 0 && selected != -1 && selectedSlider == -1) {
			Utils.playClickSound(true);

			selectedSlider = selected;
		}

		return super.mouseClicked(info, button);
	}

	@Override
	public boolean mouseReleasedAnywhere(ComponentRenderInfo info, int button, boolean inside) {
		if(button == 0 && selectedSlider != -1) {
			selectedSlider = -1;
			return true;
		}

		return super.mouseReleasedAnywhere(info, button, inside);
	}

	private int getSelectedRGBComponent(ComponentRenderInfo info) {

		for(int component = 0; component < 4; component++) {
			Rectangle rectangle = new Rectangle(RGB_OFFSET_LEFT, RGB_OFFSET_TOP + component * RGB_SPACING, 256, 11);

			if(rectangle.contains(info.getRelativeMouseX(), info.getRelativeMouseY())) {
				return component;
			}
		}

		return -1;
	}

}
