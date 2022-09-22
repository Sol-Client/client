package io.github.solclient.client.util.data;

import java.awt.Color;

import com.google.gson.annotations.Expose;

import io.github.solclient.client.platform.mc.render.GlStateManager;
import io.github.solclient.client.util.Utils;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@ToString
@EqualsAndHashCode
public class Colour {

	@Getter
	@Expose
	private final int value;

	public static final Colour WHITE =				new Colour(0xFFFFFFFF);
	public static final Colour BLACK = 				new Colour(0xFF000000);
	public static final Colour PURE_RED = 			new Colour(0xFFFF0000);
	public static final Colour PURE_GREEN = 		new Colour(0xFF00FF00);
	public static final Colour PURE_BLUE = 			new Colour(0xFF0000FF);
	public static final Colour RED_HOVER = 			new Colour(0xFFFF5050);
	public static final Colour BLUE = 				new Colour(0xFF0096FF);
	public static final Colour BLUE_HOVER = 		new Colour(0xFF1EB4FF);
	public static final Colour WHITE_128 = 			WHITE.withAlpha(128);
	public static final Colour BLACK_128 = 			BLACK.withAlpha(128);
	public static final Colour BACKGROUND = 		new Colour(0xFF141414);
	public static final Colour DISABLED_MOD = 		new Colour(0xFF282828);
	public static final Colour DISABLED_MOD_HOVER = new Colour(0xFF323232);
	public static final Colour TRANSPARENT = 		new Colour(0);
	public static final Colour LIGHT_BUTTON = 		new Colour(0xFFC8C8C8);
	public static final Colour LIGHT_BUTTON_HOVER = WHITE;

	public Colour(int value) {
		this.value = value;
		checkRange();
	}

	public Colour(int red, int green, int blue, int alpha) {
		this(((alpha & 0xFF) << 24) |
							((red & 0xFF) << 16) |
							((green & 0xFF) << 8)  |
							(blue & 0xFF));
	}

	public Colour(int red, int green, int blue) {
		this(red, green, blue, 0xFF);
	}

	public Colour withAlpha(int alpha) {
		return new Colour(getRed(), getGreen(), getBlue(), alpha);
	}

	private void checkRange() {
		checkRange(getRed(), "red");
		checkRange(getGreen(), "green");
		checkRange(getGreen(), "blue");
		checkRange(getAlpha(), "alpha");
	}

	private void checkRange(int value, String name) {
		if(value > 0xFF || value < 0) {
			throw new IllegalStateException("Invalid range for " + name + " (" + value + ")");
		}
	}

	public int getRed() {
		return (value >> 16) & 0xFF;
	}

	public int getGreen() {
		return (value >> 8) & 0xFF;
	}

	public int getBlue() {
		return value & 0xFF;
	}

	public int getAlpha() {
		return (value >> 24) & 0xFF;
	}

	public static Colour fromHSV(float hue, float saturation, float brightness) {
		return new Colour(Color.HSBtoRGB(hue, saturation, brightness));
	}

	public float[] getHSVValues() {
		return Color.RGBtoHSB(getRed(), getGreen(), getBlue(), null);
	}

	public float getHSVHue() {
		return getHSVValues()[0];
	}

	public float getHSVSaturation() {
		return getHSVValues()[1];
	}

	public float getHSVValue() {
		return getHSVValues()[2];
	}

	public float getRedFloat() {
		return getRed() / 255F;
	}

	public float getGreenFloat() {
		return getGreen() / 255F;
	}

	public float getBlueFloat() {
		return getBlue() / 255F;
	}

	public float getAlphaFloat() {
		return getAlpha() / 255F;
	}

	public Color toAWT() {
		return new Color(value, true);
	}

	public int[] getComponents() {
		return new int[] {getRed(), getGreen(), getBlue(), getAlpha()};
	}

	public Colour multiply(float factor) {
		return new Colour(clamp((int) (getRed() * factor)), clamp((int) (getGreen() * factor)), clamp((int) (getBlue() * factor)), getAlpha());
	}

	private int clamp(int channel) {
		return Utils.clamp(channel, 0, 255);
	}

	public Colour add(int amount) {
		return new Colour(clamp(getRed() + amount), clamp(getGreen() + amount), clamp(getBlue() + amount), getAlpha());
	}

	public int getShadowValue() {
		return Utils.getShadowColour(value);
	}

	public Colour getShadow() {
		return new Colour(getShadowValue());
	}

	public Colour lerp(Colour dest, float percent) {
		return new Colour(Utils.lerpColour(value, dest.value, percent));
	}

	public double getLuminance() {
		return 0.299 * getRed() + 0.587 * getGreen() + 0.114 * getBlue();
	}

	public boolean isLight() {
		return getLuminance() > 128;
	}

	public Colour getOptimalForeground() {
		return isLight() ? Colour.BLACK : Colour.WHITE;
	}

	public boolean isShadeOfGray() {
		return getRed() == getGreen() && getRed() == getBlue();
	}

	public Colour withComponent(int component, int value) {
		switch(component) {
			case 0:
				return new Colour((this.value & 0xFF00FFFF) + (value << 16));
			case 1:
				return new Colour((this.value & 0xFFFF00FF) + (value << 8));
			case 2:
				return new Colour((this.value & 0xFFFFFF00) + value);
			case 3:
				return new Colour((this.value & 0xFFFFFF) + (value << 24));
			default:
				throw new IndexOutOfBoundsException(component + " out of bounds");
		}
	}

	public Colour withHSVHue(float hue) {
		return fromHSV(hue, getHSVSaturation(), getHSVValue()).withAlpha(getAlpha());
	}

	public Colour withHSVSaturation(float saturation) {
		return fromHSV(getHSVHue(), saturation, getHSVValue()).withAlpha(getAlpha());
	}

	public Colour withHSVValue(float value) {
		return fromHSV(getHSVHue(), getHSVSaturation(), value).withAlpha(getAlpha());
	}

	public String toHexString() {
		return String.format("#%02X%02X%02X%02X", getRed(), getGreen(), getBlue(), getAlpha());
	}

	public void bind() {
		GlStateManager.colour(getRedFloat(), getGreenFloat(), getBlueFloat(), getAlphaFloat());
	}

	public static Colour fromHexString(String text) {
		if(text.length() != 7 && text.length() != 9) {
			return null;
		}

		try {
			return new Colour(Integer.valueOf(text.substring(1, 3), 16), Integer.valueOf(text.substring(3, 5), 16),
					Integer.valueOf(text.substring(5, 7), 16), text.length() > 7 ? Integer.valueOf(text.substring(7, 9), 16) : 255);
		}
		catch(NumberFormatException error) {
			return null;
		}
	}

}
