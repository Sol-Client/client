/*
 * Sol Client - an open source Minecraft client
 * Copyright (C) 2021-2023  TheKodeToad and Contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package io.github.solclient.client.ui.screen.mods;

import java.util.LinkedList;
import java.util.function.Consumer;

import org.lwjgl.nanovg.*;

import io.github.solclient.client.Client;
import io.github.solclient.client.mod.option.ModOption;
import io.github.solclient.client.mod.option.impl.ColourOption;
import io.github.solclient.client.ui.component.ComponentRenderInfo;
import io.github.solclient.client.ui.component.controller.*;
import io.github.solclient.client.ui.component.impl.*;
import io.github.solclient.client.util.MinecraftUtils;
import io.github.solclient.client.util.data.*;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class ColourPickerDialog extends BlockComponent {

	private static final int TEXT_WIDTH = 76;
	private static final int DIALOG_HEIGHT = 210;
	private static final int SLIDER_HIGHT = 10;
	private static final int BOX_HEIGHT = 100;
	private static final int HUE_Y = BOX_HEIGHT + 8;
	private static final int OPACITY_Y = HUE_Y + SLIDER_HIGHT + 6;
	private static final int PICKER_WIDTH = 150;
	private static final int PICKER_X = 22;
	private static final int PICKER_Y = 30;
	private static final int PREVIOUS_X = PICKER_X + PICKER_WIDTH + 31;
	private static final int PREVIOUS_Y = PICKER_Y + 108;

	private ModifyingState state;
	private Colour colour;
	private Consumer<Colour> callback;
	private float hue, saturation, value;
	private TextFieldComponent hex;
	private TextFieldComponent r;
	private TextFieldComponent g;
	private TextFieldComponent b;
	private TextFieldComponent a;

	public ColourPickerDialog(ColourOption option, Colour colour, Consumer<Colour> callback) {
		super(theme.bg, 12, 0);

		this.colour = colour;
		this.callback = callback;

		float[] hsv = colour.getHSVValues();
		hue = hsv[0];
		saturation = hsv[1];
		value = hsv[2];

		add(new LabelComponent(option.getName()),
				new AlignedBoundsController(Alignment.CENTRE, Alignment.START,
						(component, defaultBounds) -> new Rectangle(defaultBounds.getX(), defaultBounds.getY() + 9,
								defaultBounds.getWidth(), defaultBounds.getHeight())));

		ButtonComponent done = ButtonComponent.done(() -> {
			LinkedList<Colour> previousColours = Client.INSTANCE.getModUiState().getPreviousColours();
			if (previousColours.contains(this.colour))
				previousColours.remove(this.colour);
			previousColours.addFirst(this.colour);
			parent.setDialog(null);
		});

		add(done,
				new AlignedBoundsController(Alignment.CENTRE, Alignment.END,
						(component, defaultBounds) -> new Rectangle(
								defaultBounds.getX() - (option.canApplyToAll() ? 53 : 0),
								defaultBounds.getY() - 8, defaultBounds.getWidth(), defaultBounds.getHeight())));

		if (option.canApplyToAll()) {
			add(new ButtonComponent("sol_client.mod.screen.apply_to_all", theme.button(), theme.fg())
					.withIcon("apply_all").onClick((info, button) -> {
						if (button == 0) {
							parent.setDialog(null);
							MinecraftUtils.playClickSound(true);
							option.applyToAll();
							return true;
						}

						return false;
					}),
					new AlignedBoundsController(Alignment.CENTRE, Alignment.END,
							(component, defaultBounds) -> new Rectangle(defaultBounds.getX() + 53,
									defaultBounds.getY() - 8, defaultBounds.getWidth(), defaultBounds.getHeight())));
		}

		hex = new TextFieldComponent(TEXT_WIDTH, true);
		add(hex, (component, defaultBounds) -> defaultBounds.offset(PICKER_X + PICKER_WIDTH + 45, 59));
		hex.setText(colour.toHexString());
		hex.onUpdate((text) -> {
			Colour newColour = Colour.fromHexString(text);
			if (newColour == null)
				return false;
			this.colour = newColour;
			fieldChange();
			return true;
		});

		r = new TextFieldComponent(24, true);
		add(r, (component, defaultBounds) -> defaultBounds.offset(PICKER_X + PICKER_WIDTH + 45, 78));
		r.setText(Integer.toString(colour.getRed()));
		r.onUpdate((text) -> setRGBA(0, text));

		g = new TextFieldComponent(24, true);
		add(g, (component, defaultBounds) -> defaultBounds.offset(PICKER_X + PICKER_WIDTH + 71, 78));
		g.setText(Integer.toString(colour.getGreen()));
		g.onUpdate((text) -> setRGBA(1, text));

		b = new TextFieldComponent(24, true);
		add(b, (component, defaultBounds) -> defaultBounds.offset(PICKER_X + PICKER_WIDTH + 97, 78));
		b.setText(Integer.toString(colour.getBlue()));
		b.onUpdate((text) -> setRGBA(2, text));

		a = new TextFieldComponent(TEXT_WIDTH, true);
		add(a, (component, defaultBounds) -> defaultBounds.offset(PICKER_X + PICKER_WIDTH + 45, 97));
		a.setText(Integer.toString(colour.getAlpha()));
		a.onUpdate((text) -> setRGBA(3, text));

		this.colour = colour;
		this.callback = callback;
	}

	private boolean setRGBA(int index, String value) {
		try {
			colour = colour.withComponent(index, Integer.parseInt(value));
		} catch (NumberFormatException error) {
			return false;
		}
		fieldChange();
		return true;
	}

	@Override
	public void render(ComponentRenderInfo info) {
		super.render(info);

		NanoVG.nvgStrokeWidth(nvg, 1);

		if (state == ModifyingState.SV) {
			saturation = info.relativeMouseX() - PICKER_X;
			value = info.relativeMouseY() - PICKER_Y;
			saturation /= PICKER_WIDTH;
			value /= BOX_HEIGHT;
			value = 1 - value;
			saturation = MathHelper.clamp(saturation, 0, 1);
			value = MathHelper.clamp(value, 0, 1);

			hsvChange();
		} else if (state == ModifyingState.HUE) {
			hue = info.relativeMouseX() - PICKER_X;
			hue /= PICKER_WIDTH;
			hue = MathHelper.clamp(hue, 0, 1);

			hsvChange();
		} else if (state == ModifyingState.OPACITY) {
			float opacity = info.relativeMouseX() - PICKER_X;
			opacity /= PICKER_WIDTH;
			opacity = MathHelper.clamp(opacity, 0, 1);
			opacity *= 255;

			colour = colour.withAlpha((int) opacity);
			change();
		}

		NanoVG.nvgBeginPath(nvg);
		NanoVG.nvgRect(nvg, PICKER_X, PICKER_Y, PICKER_WIDTH, BOX_HEIGHT);

		NanoVG.nvgFillColor(nvg, Colour.fromHSV(hue, 1, 1).nvg());
		NanoVG.nvgFill(nvg);

		NVGPaint paint = NVGPaint.create();
		NanoVG.nvgLinearGradient(nvg, PICKER_X, PICKER_Y, PICKER_X + PICKER_WIDTH, PICKER_Y, Colour.WHITE.nvg(),
				Colour.TRANSPARENT.nvg(), paint);
		NanoVG.nvgFillPaint(nvg, paint);
		NanoVG.nvgFill(nvg);

		NanoVG.nvgLinearGradient(nvg, PICKER_X, PICKER_Y, PICKER_X, PICKER_Y + BOX_HEIGHT, Colour.TRANSPARENT.nvg(),
				Colour.BLACK.nvg(), paint);
		NanoVG.nvgFillPaint(nvg, paint);
		NanoVG.nvgFill(nvg);

		float selectedX = saturation * PICKER_WIDTH;
		float selectedY = (1 - value) * BOX_HEIGHT;

		if (colour.isLight())
			NanoVG.nvgStrokeColor(nvg, Colour.BLACK.nvg());
		else
			NanoVG.nvgStrokeColor(nvg, Colour.WHITE.nvg());

		NanoVG.nvgBeginPath(nvg);
		NanoVG.nvgCircle(nvg, PICKER_X + selectedX, PICKER_Y + selectedY, 3);

		NanoVG.nvgFillColor(nvg, colour.withAlpha(255).nvg());
		NanoVG.nvgFill(nvg);
		NanoVG.nvgStroke(nvg);

		NanoVG.nvgBeginPath(nvg);
		NanoVG.nvgRect(nvg, PICKER_X, PICKER_Y + HUE_Y, PICKER_WIDTH, SLIDER_HIGHT);
		NanoVG.nvgFillPaint(nvg,
				MinecraftUtils.nvgMinecraftTexturePaint(nvg, new Identifier("sol_client", "textures/gui/hues.png"),
						PICKER_X, PICKER_Y + HUE_Y, PICKER_WIDTH, SLIDER_HIGHT, 0));
		NanoVG.nvgFill(nvg);

		float hueX = hue * PICKER_WIDTH;

		NanoVG.nvgBeginPath(nvg);
		NanoVG.nvgRoundedRect(nvg, PICKER_X + hueX - 2, PICKER_Y + HUE_Y - 1, 4, SLIDER_HIGHT + 2, 1);
		NanoVG.nvgFillColor(nvg, theme.fg.nvg());
		NanoVG.nvgFill(nvg);

		MinecraftUtils.renderCheckerboard(nvg, theme.transparent1, theme.transparent2, PICKER_X, PICKER_Y + OPACITY_Y,
				30, 2, 5);

		NanoVG.nvgBeginPath(nvg);
		NanoVG.nvgRect(nvg, PICKER_X, PICKER_Y + OPACITY_Y, PICKER_WIDTH, SLIDER_HIGHT);
		NanoVG.nvgLinearGradient(nvg, PICKER_X, 0, PICKER_X + PICKER_WIDTH, 0, Colour.TRANSPARENT.nvg(),
				Colour.WHITE.nvg(), paint);
		NanoVG.nvgFillPaint(nvg, paint);
		NanoVG.nvgFill(nvg);

		float opacityX = colour.getAlpha() / 255F * PICKER_WIDTH;

		NanoVG.nvgBeginPath(nvg);
		NanoVG.nvgRoundedRect(nvg, PICKER_X + opacityX - 2, PICKER_Y + OPACITY_Y - 1, 4, SLIDER_HIGHT + 2, 1);
		NanoVG.nvgFillColor(nvg, theme.fg.nvg());
		NanoVG.nvgFill(nvg);

		NanoVG.nvgFillColor(nvg, theme.fg.nvg());
		regularFont.renderString(nvg, I18n.translate("sol_client.hex"), PICKER_X + PICKER_WIDTH + 12, PICKER_Y + 31.5F);
		regularFont.renderString(nvg, I18n.translate("sol_client.rgb"), PICKER_X + PICKER_WIDTH + 12, PICKER_Y + 50.5F);
		regularFont.renderString(nvg, I18n.translate("sol_client.alpha"), PICKER_X + PICKER_WIDTH + 12,
				PICKER_Y + 69.5F);

		MinecraftUtils.renderCheckerboard(nvg, theme.transparent1, theme.transparent2, PICKER_X + PICKER_WIDTH + 12,
				PICKER_Y, 27, 5, 4);

		NanoVG.nvgBeginPath(nvg);
		NanoVG.nvgRect(nvg, PICKER_X + PICKER_WIDTH + 12, PICKER_Y, 108, 20);
		NanoVG.nvgFillColor(nvg, colour.nvg());
		NanoVG.nvgFill(nvg);

		NanoVG.nvgFillColor(nvg, Colour.WHITE.nvg());
		NanoVG.nvgStrokeColor(nvg, Colour.WHITE.nvg());

		String previousText = I18n.translate("sol_client.previous_colours");
		regularFont.renderString(nvg, previousText, PREVIOUS_X + 35 - regularFont.getWidth(nvg, previousText) / 2,
				PREVIOUS_Y - 14);

		int index = 0;
		for (int y = 0; y < 2; y++) {
			for (int x = 0; x < 5; x++) {
				Colour colour = Client.INSTANCE.getModUiState().getPreviousColours().get(index);

				NanoVG.nvgBeginPath(nvg);
				NanoVG.nvgCircle(nvg, PREVIOUS_X + x * 15 + 5, PREVIOUS_Y + y * 15 + 5, 5);
				NanoVG.nvgFillColor(nvg, colour.nvg());
				NanoVG.nvgFill(nvg);

				if (colour.needsOutline(theme.bg))
					NanoVG.nvgStroke(nvg);

				index++;
			}
		}
	}

	private void hsvChange() {
		colour = Colour.fromHSV(hue, saturation, value).withAlpha(colour.getAlpha());
		change();
	}

	private void fieldChange() {
		float[] hsv = colour.getHSVValues();
		hue = hsv[0];
		saturation = hsv[1];
		value = hsv[2];
		change();
	}

	private void change() {
		updateFields();
		accept();
	}

	private void updateFields() {
		hex.setText(colour.toHexString());
		r.setText(Integer.toString(colour.getRed()));
		g.setText(Integer.toString(colour.getGreen()));
		b.setText(Integer.toString(colour.getBlue()));
		a.setText(Integer.toString(colour.getAlpha()));
	}

	private void accept() {
		callback.accept(colour);
	}

	@Override
	public boolean mouseClicked(ComponentRenderInfo info, int button) {
		if ((button == 0)) {
			if (state == null && info.relativeMouseX() >= PICKER_X
					&& info.relativeMouseX() <= PICKER_X + PICKER_WIDTH) {
				if (info.relativeMouseY() >= PICKER_Y && info.relativeMouseY() <= PICKER_Y + BOX_HEIGHT)
					state = ModifyingState.SV;
				else if (info.relativeMouseY() >= PICKER_Y + HUE_Y
						&& info.relativeMouseY() <= PICKER_Y + HUE_Y + SLIDER_HIGHT)
					state = ModifyingState.HUE;
				else if (info.relativeMouseY() >= PICKER_Y + OPACITY_Y
						&& info.relativeMouseY() <= PICKER_Y + OPACITY_Y + SLIDER_HIGHT)
					state = ModifyingState.OPACITY;
			} else if (info.relativeMouseX() >= PREVIOUS_X && info.relativeMouseX() <= PREVIOUS_X + 70
					&& info.relativeMouseY() >= PREVIOUS_Y && info.relativeMouseY() <= PREVIOUS_Y + 25
					&& (info.relativeMouseX() - PREVIOUS_X) % 15 <= 10
					&& (info.relativeMouseY() - PREVIOUS_Y) % 15 <= 10) {
				// huh who uses object oriented programming these days mouse coordinate
				// manipulation is so much more fun
				int x = (int) ((info.relativeMouseX() - PREVIOUS_X) / 15);
				int y = (int) ((info.relativeMouseY() - PREVIOUS_Y) / 15);
				int index = x + y * 5;
				colour = Client.INSTANCE.getModUiState().getPreviousColours().get(index);
				fieldChange();
			}
		}

		return super.mouseClicked(info, button);
	}

	@Override
	public boolean mouseReleasedAnywhere(ComponentRenderInfo info, int button, boolean inside) {
		if (state != null)
			state = null;

		return super.mouseReleasedAnywhere(info, button, inside);
	}

	@Override
	protected Rectangle getDefaultBounds() {
		return Rectangle.ofDimensions(300, DIALOG_HEIGHT);
	}

	private static enum ModifyingState {
		HUE, SV, OPACITY
	}

}
