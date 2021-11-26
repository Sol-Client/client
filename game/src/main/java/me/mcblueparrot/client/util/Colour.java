package me.mcblueparrot.client.util;

import java.awt.Color;

import com.google.gson.annotations.Expose;

import lombok.Getter;
import net.minecraft.util.MathHelper;

public class Colour {

	@Getter
	@Expose
	private int value;

	public static final Colour WHITE = new Colour(255, 255, 255);
	public static final Colour BLACK = new Colour(0, 0, 0);
	public static final Colour RED = new Colour(255, 0, 0);
	public static final Colour BLUE = new Colour(0, 150, 255);
	public static final Colour WHITE_128 = WHITE.withAlpha(128);
	public static final Colour BLACK_128 = BLACK.withAlpha(128);

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
		this(red, green, blue, 255);
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
		if(value > 255 || value < 0) {
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
		return MathHelper.clamp_int(channel, 0, 255);
	}

	public Colour add(int amount) {
		return new Colour(clamp(getRed() + amount), clamp(getGreen() + amount), clamp(getBlue() + amount), getAlpha());
	}

	public int getShadowValue() {
		return Utils.getShadowColour(getValue());
	}

	public Colour getShadow() {
		return new Colour(getShadowValue());
	}

}
