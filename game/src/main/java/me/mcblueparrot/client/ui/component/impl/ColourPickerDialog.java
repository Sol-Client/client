package me.mcblueparrot.client.ui.component.impl;

import java.awt.Color;
import java.util.function.Consumer;

import me.mcblueparrot.client.Client;
import me.mcblueparrot.client.mod.CachedConfigOption;
import me.mcblueparrot.client.mod.Mod;
import me.mcblueparrot.client.mod.impl.SolClientMod;
import me.mcblueparrot.client.ui.component.ComponentRenderInfo;
import me.mcblueparrot.client.ui.component.controller.AlignedBoundsController;
import me.mcblueparrot.client.ui.component.controller.AnimatedColourController;
import me.mcblueparrot.client.util.Utils;
import me.mcblueparrot.client.util.data.Alignment;
import me.mcblueparrot.client.util.data.Colour;
import me.mcblueparrot.client.util.data.Rectangle;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.MathHelper;

public class ColourPickerDialog extends ScaledIconComponent {

	private Colour colour;
	private Consumer<Colour> callback;
	private int selectedSlider = -1;

	private static final int RGB_OFFSET_TOP = 24;
	private static final int RGB_OFFSET_LEFT = 22;
	private static final int RGB_SPACING = 20;

	private TextFieldComponent hex;

	//	private boolean hsv;
//	private float hue;

	public ColourPickerDialog(CachedConfigOption colourOption, Colour colour, Consumer<Colour> callback) {
		super("sol_client_colour_dialog", 300, 150, (component, defaultColour) -> new Colour(40, 40, 40));
		add(new LabelComponent(colourOption.name),
				new AlignedBoundsController(Alignment.CENTRE, Alignment.START,
						(component, defaultBounds) -> new Rectangle(defaultBounds.getX(), defaultBounds.getY() + 10,
								defaultBounds.getWidth(), defaultBounds.getHeight())));

		//		add(new ScaledIconComponent("sol_client_rgb", 16, 16, new AnimatedColourController(
		//				(component, defaultColour) -> component.isHovered() || !hsv ? Colour.LIGHT_BUTTON_HOVER : Colour.LIGHT_BUTTON))
		//						.onClick((info, button) -> {
		//							if(button == 0) {
		//								Utils.playClickSound(true);
		//								hsv = false;
		//								return true;
		//							}
		//
		//							return false;
		//						}),
		//				new AlignedBoundsController(Alignment.CENTRE, Alignment.START,
		//						(component, defaultBounds) -> new Rectangle(defaultBounds.getX() - 10, defaultBounds.getY() + 24,
		//								defaultBounds.getWidth(), defaultBounds.getHeight())));
		//
		//		add(new ScaledIconComponent("sol_client_hsv", 16, 16, new AnimatedColourController(
		//				(component, defaultColour) -> component.isHovered() || hsv ? Colour.LIGHT_BUTTON_HOVER : Colour.LIGHT_BUTTON))
		//						.onClick((info, button) -> {
		//							if(button == 0) {
		//								Utils.playClickSound(true);
		//								hsv = true;
		//								return true;
		//							}
		//
		//							return false;
		//						}),
		//				new AlignedBoundsController(Alignment.CENTRE, Alignment.START,
		//						(component, defaultBounds) -> new Rectangle(defaultBounds.getX() + 10, defaultBounds.getY() + 24,
		//								defaultBounds.getWidth(), defaultBounds.getHeight())));

		add(ButtonComponent.done(() -> {
			hex.flush();
			parent.setDialog(null);
		}), new AlignedBoundsController(Alignment.CENTRE, Alignment.END,
				(component, defaultBounds) -> new Rectangle(defaultBounds.getX() - (colourOption.common ? 50 : 0), defaultBounds.getY() - 5,
						defaultBounds.getWidth(), defaultBounds.getHeight())));

		if(colourOption.common) {
			add(new ButtonComponent("Apply to All",
					new AnimatedColourController(
							(component, defaultColour) -> component.isHovered() ? Colour.BLUE_HOVER : Colour.BLUE))
									.withIcon("sol_client_new")
									.onClick((info, button) -> {
										if(button == 0) {
											hex.flush();
											parent.setDialog(null);
											Utils.playClickSound(true);
											for(Mod mod : Client.INSTANCE.getMods()) {
												for(CachedConfigOption option : mod.getOptions()) {
													if(option.name.equals(colourOption.name)) {
														option.setValue(this.colour);
													}
												}
											}
											return true;
										}

										return false;
									}),
					new AlignedBoundsController(Alignment.CENTRE, Alignment.END,
							(component, defaultBounds) -> new Rectangle(defaultBounds.getX() + 50, defaultBounds.getY() - 5,
									defaultBounds.getWidth(), defaultBounds.getHeight())));
		}

		add(hex = new TextFieldComponent(60, true), new AlignedBoundsController(Alignment.CENTRE, Alignment.END, (component, defaultBounds) -> new Rectangle(defaultBounds.getX(), defaultBounds.getY() - 30, defaultBounds.getWidth(), defaultBounds.getHeight())));

		this.colour = colour;
		this.callback = callback;

		updateHex();
		hex.onUpdate((text) -> {
			Colour parsed = Colour.fromHexString(text);

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
	public void render(ComponentRenderInfo info) {
		super.render(info);

//		if(colour.getHSVHue() != 0) {
//			hue = colour.getHSVHue();
//		}
//		else {
//			colour = colour.withHSVHue(hue);
//		}

		//		if(!hsv) {
		if(selectedSlider != -1) {
			colour = colour.withComponent(selectedSlider, MathHelper.clamp_int(info.getRelativeMouseX() - RGB_OFFSET_LEFT, 0, 255));
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

				Utils.drawVerticalLine(RGB_OFFSET_LEFT + i, RGB_OFFSET_TOP + component * RGB_SPACING, RGB_OFFSET_TOP + (component * RGB_SPACING) + 11, stripColour.getValue());
			}
		}
		//		}
		//		else {
		//			for(float h = 0; h < 1; h += 0.001) {
		//				Utils.drawHorizontalLine(0, 10, (int) (h * 100), Colour.fromHSV(h, 1, 1).getValue());
		//			}
		//
		//			for(float s = 0; s < 1; s += 0.01F) {
		//				for(float v = 0; v < 1; v += 0.002F) {
		//					int x = 30 + (int) (s * 50);
		//					int y = 50 - (int) (v * 50);
		//					Gui.drawRect(x, y, x + 1, y + 1, Colour.fromHSV(hue, s, v).getValue());
		//				}
		//			}
		//
		//			int hueY = (int) (hue * 100);
		//			Utils.drawHorizontalLine(0, 10, hueY, -1);
		//
		//			int saturationX = 30 + (int) (colour.getHSVSaturation() * 50) - 1;
		//			Utils.drawVerticalLine(saturationX, 0, 51, -1);
		//
		//			int valueY = 50 - (int) (colour.getHSVValue() * 50);
		//			Utils.drawHorizontalLine(30, 79, valueY, -1);
		//
		//			if(selectedSlider == 0) {
		//				int relativeX = MathHelper.clamp_int(info.getRelativeMouseX() - 30, 0, 50);
		//				int relativeY = 50 - MathHelper.clamp_int(info.getRelativeMouseY(), 0, 50);
		//
		//				colour = colour.withHSVSaturation(relativeX / 50F);
		//				colour = colour.withHSVValue(relativeY / 50F);
		//			}
		//			else if(selectedSlider == 1) {
		//				int relativeY = MathHelper.clamp_int(info.getRelativeMouseY(), 0, 99);
		//				colour = colour.withHSVHue(relativeY / 100F);
		//			}
		//		}
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

//	private int getSelectedHSVComponent(ComponentRenderInfo info) {
//		if(new Rectangle(30, 0, 50, 50).contains(info.getRelativeMouseX(), info.getRelativeMouseY())) {
//			return 0;
//		}
//		else if(new Rectangle(0, 0, 11, 100).contains(info.getRelativeMouseX(), info.getRelativeMouseY())) {
//			return 1;
//		}
//
//		return -1;
//	}

	@Override
	public boolean mouseReleasedAnywhere(ComponentRenderInfo info, int button, boolean inside) {
		if(button == 0 && selectedSlider != -1) {
			selectedSlider = -1;
			callback.accept(colour);
			return true;
		}

		return super.mouseReleasedAnywhere(info, button, inside);
	}

	private int getSelectedRGBComponent(ComponentRenderInfo info) {
//		if(hsv) {
//			return -1;
//		}

		for(int component = 0; component < 4; component++) {
			Rectangle rectangle = new Rectangle(RGB_OFFSET_LEFT, RGB_OFFSET_TOP + component * RGB_SPACING, 256, 11);

			if(rectangle.contains(info.getRelativeMouseX(), info.getRelativeMouseY())) {
				return component;
			}
		}

		return -1;
	}

}
