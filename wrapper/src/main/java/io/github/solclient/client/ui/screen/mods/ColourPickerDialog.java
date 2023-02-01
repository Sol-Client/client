package io.github.solclient.client.ui.screen.mods;

import java.util.function.Consumer;

import org.lwjgl.nanovg.*;

import io.github.solclient.client.mod.option.ModOption;
import io.github.solclient.client.ui.component.ComponentRenderInfo;
import io.github.solclient.client.ui.component.controller.*;
import io.github.solclient.client.ui.component.impl.*;
import io.github.solclient.client.util.MinecraftUtils;
import io.github.solclient.client.util.data.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class ColourPickerDialog extends BlockComponent {

	private static final int TEXT_WIDTH = 61;
	private static final int DIALOG_HEIGHT = 210;
	private static final int SLIDER_HIGHT = 8;
	private static final int BOX_HEIGHT = 100;
	private static final int HUE_Y = BOX_HEIGHT + 10;
	private static final int OPACITY_Y = HUE_Y + SLIDER_HIGHT + 10;
	private static final int PICKER_WIDTH = 150;
	private static final int PICKER_X = 22;
	private static final int PICKER_Y = 30;

	private ModifyingState state;
	private Colour colour;
	private Consumer<Colour> callback;
	private float hue, saturation, value;
	private TextFieldComponent hex;
	private TextFieldComponent r;
	private TextFieldComponent g;
	private TextFieldComponent b;
	private TextFieldComponent a;

	public ColourPickerDialog(ModOption<Colour> colourOption, Colour colour, Consumer<Colour> callback) {
		super(theme.bg, 12, 0);

		this.colour = colour;
		this.callback = callback;

		float[] hsv = colour.getHSVValues();
		hue = hsv[0];
		saturation = hsv[1];
		value = hsv[2];

		add(new LabelComponent(colourOption.getName()),
				new AlignedBoundsController(Alignment.CENTRE, Alignment.START,
						(component, defaultBounds) -> new Rectangle(defaultBounds.getX(), defaultBounds.getY() + 9,
								defaultBounds.getWidth(), defaultBounds.getHeight())));

		ButtonComponent done = ButtonComponent.done(() -> {
			parent.setDialog(null);
		});

		add(done,
				new AlignedBoundsController(Alignment.CENTRE, Alignment.END,
						(component, defaultBounds) -> new Rectangle(
								defaultBounds.getX() - (colourOption.canApplyToAll() ? 53 : 0),
								defaultBounds.getY() - 8, defaultBounds.getWidth(), defaultBounds.getHeight())));

		if (colourOption.canApplyToAll()) {
			add(new ButtonComponent("sol_client.mod.screen.apply_to_all", theme.button(), theme.fg())
					.withIcon("apply_all").onClick((info, button) -> {
						if (button == 0) {
							parent.setDialog(null);
							MinecraftUtils.playClickSound(true);
							colourOption.applyToAll();
							return true;
						}

						return false;
					}),
					new AlignedBoundsController(Alignment.CENTRE, Alignment.END,
							(component, defaultBounds) -> new Rectangle(defaultBounds.getX() + 53,
									defaultBounds.getY() - 8, defaultBounds.getWidth(), defaultBounds.getHeight())));
		}

		hex = new TextFieldComponent(TEXT_WIDTH, true);
		add(hex, (component, defaultBounds) -> defaultBounds.offset(PICKER_X + PICKER_WIDTH + 45, 54));
		hex.setText(colour.toHexString());
		hex.onUpdate((text) -> {
			Colour newColour = Colour.fromHexString(text);
			if (newColour == null)
				return false;
			this.colour = newColour;
			fieldChange();
			return true;
		});

		r = new TextFieldComponent(TEXT_WIDTH, true);
		add(r, (component, defaultBounds) -> defaultBounds.offset(PICKER_X + PICKER_WIDTH + 45, 79));
		r.setText(Integer.toString(colour.getRed()));
		r.onUpdate((text) -> setRGBA(0, text));

		g = new TextFieldComponent(TEXT_WIDTH, true);
		add(g, (component, defaultBounds) -> defaultBounds.offset(PICKER_X + PICKER_WIDTH + 45, 104));
		g.setText(Integer.toString(colour.getGreen()));
		g.onUpdate((text) -> setRGBA(1, text));

		b = new TextFieldComponent(TEXT_WIDTH, true);
		add(b, (component, defaultBounds) -> defaultBounds.offset(PICKER_X + PICKER_WIDTH + 45, 129));
		b.setText(Integer.toString(colour.getBlue()));
		b.onUpdate((text) -> setRGBA(2, text));

		a = new TextFieldComponent(TEXT_WIDTH, true);
		add(a, (component, defaultBounds) -> defaultBounds.offset(PICKER_X + PICKER_WIDTH + 45, 154));
		a.setText(Integer.toString(colour.getBlue()));
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

		NanoVG.nvgStrokeWidth(nvg, 1);

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
		NanoVG.nvgRect(nvg, PICKER_X + hueX - 2, PICKER_Y + HUE_Y - 1, 4, SLIDER_HIGHT + 2);

		NanoVG.nvgFillColor(nvg, Colour.fromHSV(hue, 1, 1).nvg());
		NanoVG.nvgFill(nvg);
		NanoVG.nvgStrokeColor(nvg, Colour.WHITE.nvg());
		NanoVG.nvgStroke(nvg);

		NanoVG.nvgBeginPath(nvg);
		NanoVG.nvgRect(nvg, PICKER_X, PICKER_Y + OPACITY_Y, PICKER_WIDTH, SLIDER_HIGHT);
		NanoVG.nvgLinearGradient(nvg, PICKER_X, 0, PICKER_X + PICKER_WIDTH, 0, Colour.TRANSPARENT.nvg(),
				Colour.WHITE.nvg(), paint);
		NanoVG.nvgFillPaint(nvg, paint);
		NanoVG.nvgFill(nvg);

		float opacityX = colour.getAlpha() / 255F * PICKER_WIDTH;

		NanoVG.nvgBeginPath(nvg);
		NanoVG.nvgRect(nvg, PICKER_X + opacityX - 2, PICKER_Y + OPACITY_Y - 1, 4, SLIDER_HIGHT + 2);

		NanoVG.nvgFillColor(nvg, theme.bg.lerp(Colour.WHITE, colour.getAlphaFloat()).nvg());
		NanoVG.nvgFill(nvg);
		if (colour.getAlpha() > 128)
			NanoVG.nvgStrokeColor(nvg, Colour.BLACK.nvg());
		else
			NanoVG.nvgStrokeColor(nvg, Colour.WHITE.nvg());
		NanoVG.nvgStroke(nvg);

		NanoVG.nvgFillColor(nvg, theme.fg.nvg());
		regularFont.renderString(nvg, "Preview", PICKER_X + PICKER_WIDTH + 12, PICKER_Y + 1);
		regularFont.renderString(nvg, "Hex", PICKER_X + PICKER_WIDTH + 12, PICKER_Y + 26);
		regularFont.renderString(nvg, "Red", PICKER_X + PICKER_WIDTH + 12, PICKER_Y + 51);
		regularFont.renderString(nvg, "Green", PICKER_X + PICKER_WIDTH + 12, PICKER_Y + 76);
		regularFont.renderString(nvg, "Blue", PICKER_X + PICKER_WIDTH + 12, PICKER_Y + 101);
		regularFont.renderString(nvg, "Alpha", PICKER_X + PICKER_WIDTH + 12, PICKER_Y + 126);


		NanoVG.nvgBeginPath(nvg);
		NanoVG.nvgRect(nvg, PICKER_X + 200, PICKER_Y - 4, 50, 19);
		NanoVG.nvgFillColor(nvg, colour.nvg());
		NanoVG.nvgFill(nvg);

		NanoVG.nvgFillColor(nvg, Colour.WHITE.nvg());
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
		if (button == 0 && state == null && info.relativeMouseX() > PICKER_X
				&& info.relativeMouseX() <= PICKER_X + PICKER_WIDTH) {
			if (info.relativeMouseY() > PICKER_Y && info.relativeMouseY() <= PICKER_Y + BOX_HEIGHT)
				state = ModifyingState.SV;
			else if (info.relativeMouseY() > PICKER_Y + HUE_Y
					&& info.relativeMouseY() <= PICKER_Y + HUE_Y + SLIDER_HIGHT)
				state = ModifyingState.HUE;
			else if (info.relativeMouseY() > PICKER_Y + OPACITY_Y
					&& info.relativeMouseY() < PICKER_Y + OPACITY_Y + SLIDER_HIGHT)
				state = ModifyingState.OPACITY;
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
