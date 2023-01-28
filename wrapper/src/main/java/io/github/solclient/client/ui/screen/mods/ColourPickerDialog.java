package io.github.solclient.client.ui.screen.mods;

import java.util.function.Consumer;

import org.lwjgl.nanovg.*;

import io.github.solclient.client.mod.ModOption;
import io.github.solclient.client.ui.component.ComponentRenderInfo;
import io.github.solclient.client.ui.component.controller.*;
import io.github.solclient.client.ui.component.impl.*;
import io.github.solclient.client.util.MinecraftUtils;
import io.github.solclient.client.util.data.*;
import net.minecraft.util.math.MathHelper;

public class ColourPickerDialog extends BlockComponent {

	private Colour colour;
	private final Consumer<Colour> callback;
	private int selectedSlider = -1;

	private static final int RGB_OFFSET_TOP = 24;
	private static final int RGB_OFFSET_LEFT = 22;
	private static final int RGB_SPACING = 20;

	private final TextFieldComponent hex;
	private final ButtonComponent done;

	public ColourPickerDialog(ModOption colourOption, Colour colour, Consumer<Colour> callback) {
		super(theme.bg, 12, 0);

		add(hex = new TextFieldComponent(60, true),
				new AlignedBoundsController(Alignment.CENTRE, Alignment.END,
						(component, defaultBounds) -> new Rectangle(defaultBounds.getX(), defaultBounds.getY() - 32,
								defaultBounds.getWidth(), defaultBounds.getHeight())));

		add(new LabelComponent(colourOption.getName()),
				new AlignedBoundsController(Alignment.CENTRE, Alignment.START,
						(component, defaultBounds) -> new Rectangle(defaultBounds.getX(), defaultBounds.getY() + 9,
								defaultBounds.getWidth(), defaultBounds.getHeight())));

		add(done = ButtonComponent.done(() -> {
			hex.flush();
			parent.setDialog(null);
		}), new AlignedBoundsController(Alignment.CENTRE, Alignment.END,
				(component, defaultBounds) -> new Rectangle(
						defaultBounds.getX() - (colourOption.canApplyToAll() ? 53 : 0), defaultBounds.getY() - 5,
						defaultBounds.getWidth(), defaultBounds.getHeight())));

		if (colourOption.canApplyToAll()) {
			add(new ButtonComponent("sol_client.mod.screen.apply_to_all", theme.button(), theme.fg())
					.withIcon("apply_all").onClick((info, button) -> {
						if (button == 0) {
							hex.flush();
							parent.setDialog(null);
							MinecraftUtils.playClickSound(true);
							colourOption.applyToAll();
							return true;
						}

						return false;
					}),
					new AlignedBoundsController(Alignment.CENTRE, Alignment.END,
							(component, defaultBounds) -> new Rectangle(defaultBounds.getX() + 53,
									defaultBounds.getY() - 5, defaultBounds.getWidth(), defaultBounds.getHeight())));
		}

		add(new ColourBoxComponent((component, defaultColour) -> this.colour),
				(component, defaultBounds) -> new Rectangle(done.getBounds().getX() - 20, done.getBounds().getY() + 2,
						defaultBounds.getWidth(), defaultBounds.getHeight()));

		this.colour = colour;
		this.callback = callback;

		updateHex();
		hex.onUpdate((text) -> {
			Colour parsed = Colour.fromHexString(text);

			if (parsed != null) {
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

		if (selectedSlider != -1) {
			colour = colour.withComponent(selectedSlider,
					(int) MathHelper.clamp(info.getRelativeMouseX() - RGB_OFFSET_LEFT, 0, 255));
			callback.accept(colour);
			updateHex();
		}

		for (int component = 0; component < 4; component++) {
			Rectangle rectangle = new Rectangle(RGB_OFFSET_LEFT, RGB_OFFSET_TOP + component * RGB_SPACING + 1, 256, 10);

			if (component == 3) {
				for (int x = 0; x < 250; x += 10) {
					NanoVG.nvgBeginPath(nvg);
					NanoVG.nvgFillColor(nvg, new Colour(70, 70, 70).nvg());
					NanoVG.nvgRect(nvg, rectangle.getX() + x, rectangle.getY(), 5, 5);
					NanoVG.nvgFill(nvg);

					NanoVG.nvgBeginPath(nvg);
					NanoVG.nvgFillColor(nvg, new Colour(70, 70, 70).nvg());
					NanoVG.nvgRect(nvg, rectangle.getX() + x + 5, rectangle.getY() + 5, 5, 5);
					NanoVG.nvgFill(nvg);
				}

				for (int x = 0; x < 250; x += 10) {
					NanoVG.nvgBeginPath(nvg);
					NanoVG.nvgFillColor(nvg, new Colour(50, 50, 50).nvg());
					NanoVG.nvgRect(nvg, rectangle.getX() + x + 5, rectangle.getY(), 5, 5);
					NanoVG.nvgFill(nvg);

					NanoVG.nvgBeginPath(nvg);
					NanoVG.nvgFillColor(nvg, new Colour(50, 50, 50).nvg());
					NanoVG.nvgRect(nvg, rectangle.getX() + x, rectangle.getY() + 5, 5, 5);
					NanoVG.nvgFill(nvg);
				}
			}

			String name = "?";
			switch (component) {
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
			NanoVG.nvgFillColor(nvg, Colour.WHITE.nvg());
			regularFont.renderString(nvg, name, rectangle.getX() - 10,
					rectangle.getY() + 5 - (regularFont.getLineHeight(nvg) / 2));

			NanoVG.nvgBeginPath(nvg);
			NVGPaint paint = NVGPaint.malloc();
			NanoVG.nvgLinearGradient(nvg, rectangle.getX(), 0, rectangle.getEndX(), 0,
					Colour.BLACK.withComponent(component, 0).nvg(), Colour.BLACK.withComponent(component, 255).nvg(),
					paint);
			NanoVG.nvgFillPaint(nvg, paint);
			NanoVG.nvgRoundedRect(nvg, rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight(),
					0);
			NanoVG.nvgFill(nvg);

			paint.free();

			int value = colour.getComponents()[component];

			NanoVG.nvgBeginPath(nvg);
			NanoVG.nvgFillColor(nvg, Colour.WHITE.nvg());
			NanoVG.nvgRect(nvg, rectangle.getX() + value, rectangle.getY(), 1, rectangle.getHeight());
			NanoVG.nvgFill(nvg);

			regularFont.renderString(nvg, Integer.toString(value),
					RGB_OFFSET_LEFT + value - (regularFont.getWidth(nvg, Integer.toString(value)) / 2),
					rectangle.getY() + 9);

//
//			for (int i = 0; i < 256; i++) {
//				Colour stripColour = Colour.BLACK.withComponent(component, i);
//
//				if (colour.getComponents()[component] == i) {
//					stripColour = Colour.WHITE;
//
//					NanoVG.nvgFillColor(nvg, Colour.WHITE.nvg());
//				}
//
//				Utils.drawVerticalLine(RGB_OFFSET_LEFT + i, RGB_OFFSET_TOP + component * RGB_SPACING,
//						RGB_OFFSET_TOP + (component * RGB_SPACING) + 11, stripColour.getValue());
//			}
		}
	}

	@Override
	public boolean mouseClicked(ComponentRenderInfo info, int button) {
		int selected = getSelectedRGBComponent(info);

		if (button == 0 && selected != -1 && selectedSlider == -1) {
			MinecraftUtils.playClickSound(true);

			selectedSlider = selected;
		}

		return super.mouseClicked(info, button);
	}

	@Override
	public boolean mouseReleasedAnywhere(ComponentRenderInfo info, int button, boolean inside) {
		if (button == 0 && selectedSlider != -1) {
			selectedSlider = -1;
			return true;
		}

		return super.mouseReleasedAnywhere(info, button, inside);
	}

	private int getSelectedRGBComponent(ComponentRenderInfo info) {
		for (int component = 0; component < 4; component++) {
			Rectangle rectangle = new Rectangle(RGB_OFFSET_LEFT, RGB_OFFSET_TOP + component * RGB_SPACING, 256, 11);

			if (rectangle.contains((int) info.getRelativeMouseX(), (int) info.getRelativeMouseY()))
				return component;
		}

		return -1;
	}

	@Override
	protected Rectangle getDefaultBounds() {
		return Rectangle.ofDimensions(300, 150);
	}

}
