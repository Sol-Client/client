package io.github.solclient.client.util.data;

import java.util.BitSet;

import org.lwjgl.nanovg.*;

import com.google.gson.annotations.Expose;
import com.mojang.blaze3d.platform.GlStateManager;

import io.github.solclient.client.util.*;
import lombok.Getter;
import net.minecraft.client.texture.NativeImageBackedTexture;

public final class PixelMatrix {

	@Getter
	@Expose
	private int width, height;
	@Expose
	private BitSet pixels;
	private transient NativeImageBackedTexture texture;
	private transient int nvgImage;
	private transient int lastHash;

	public PixelMatrix(int width, int height) {
		this.width = width;
		this.height = height;
		pixels = new BitSet(width * height);
	}

	public int getIndex(int x, int y) {
		return y * width + x;
	}

	public boolean get(int x, int y) {
		return get(getIndex(x, y));
	}

	public boolean get(int pixel) {
		return pixels.get(pixel);
	}

	public void set(int x, int y) {
		set(getIndex(x, y));
	}

	public void set(int pixel) {
		pixels.set(pixel);
	}

	public void clear(int x, int y) {
		clear(getIndex(x, y));
	}

	public void clear(int pixel) {
		pixels.clear(pixel);
	}

	public void clear() {
		pixels.clear();
	}

	public int pixels() {
		return width * height;
	}

	/**
	 * Gets this as a GL image.
	 *
	 * @param fg the colour to use when <code>true</code>.
	 * @param bg the colour to use when <code>false</code>.
	 */
	public int getTexture(int fg, int bg) {
		// thanks AxolotlClient!
		// I didn't know NativeImageBackedTexture existed
		/// lazy load for those wishing to use this class just for pixel storage
		if (texture == null)
			texture = new NativeImageBackedTexture(width, height);

		// reupload
		int hash = hashCode();
		hash = 31 * hash + fg;
		hash = 31 * hash + bg;

		if (hash != lastHash) {
			lastHash = hash;

			for (int i = 0; i < texture.getPixels().length; i++)
				texture.getPixels()[i] = get(i) ? fg : bg;

			texture.upload();
			nvgImage = NanoVGGL3.nvglCreateImageFromHandle(NanoVGManager.getNvg(), texture.getGlId(), width, height, 0);
		}

		return texture.getGlId();
	}

	public void bind(int fg, int bg) {
		GlStateManager.bindTexture(getTexture(fg, bg));
	}

	public void nvgBind(long nvg, int x, int y, int fg, int bg) {
		getTexture(fg, bg);
		NanoVG.nvgFillPaint(nvg, MinecraftUtils.nvgTexturePaint(nvg, nvgImage, x, y, width, height));
	}

	@Override
	public int hashCode() {
		return pixels.hashCode();
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++)
				result.append(get(x, y) ? '#' : ' ');
			result.append('\n');
		}
		if (height != 0)
			result.deleteCharAt(result.length() - 1);
		return result.toString();
	}

}
