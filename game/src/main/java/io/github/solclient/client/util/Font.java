package io.github.solclient.client.util;

import java.io.*;
import java.nio.ByteBuffer;

import org.lwjgl.nanovg.NanoVG;
import org.lwjgl.system.MemoryUtil;

public final class Font {

	private final int handle;
	private final ByteBuffer buffer;

	public Font(long ctx, InputStream in) throws IOException {
		buffer = Utils.mallocAndRead(in);
		handle = NanoVG.nvgCreateFontMem(ctx, "", buffer, 0);
	}

	public void bind(long ctx) {
		NanoVG.nvgFontFaceId(ctx, handle);
	}

	public void close() {
		MemoryUtil.memFree(buffer);
	}

	public float getWidth(long ctx, String string) {
		bind(ctx);
		float[] bounds = new float[4];
		NanoVG.nvgTextBounds(ctx, 0, 0, string, bounds);
		return bounds[2];
	}

	public float getLineHeight(long ctx) {
		bind(ctx);
		float[] ascender = new float[1];
		float[] descender = new float[1];
		float[] lineh = new float[1];
		NanoVG.nvgFontSize(ctx, 8);
		NanoVG.nvgTextMetrics(ctx, ascender, descender, lineh);
		return lineh[0];
	}

	public float renderString(long ctx, String string, float x, float y) {
		bind(ctx);
		return NanoVG.nvgText(ctx, x, y + getLineHeight(ctx), string);
	}

}
