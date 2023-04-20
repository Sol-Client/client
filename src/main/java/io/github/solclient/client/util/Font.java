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

package io.github.solclient.client.util;

import java.io.*;
import java.nio.ByteBuffer;

import org.lwjgl.nanovg.NanoVG;
import org.lwjgl.system.MemoryUtil;

public final class Font {

	private final int handle;
	private final ByteBuffer buffer;

	public Font(long ctx, InputStream in) throws IOException {
		buffer = MinecraftUtils.mallocAndRead(in);
		handle = NanoVG.nvgCreateFontMem(ctx, "", buffer, 0);
	}

	public void bind(long ctx) {
		NanoVG.nvgFontFaceId(ctx, handle);
		NanoVG.nvgFontSize(ctx, 8);
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
		NanoVG.nvgTextMetrics(ctx, ascender, descender, lineh);
		return lineh[0];
	}

	public float renderString(long ctx, String string, float x, float y) {
		return NanoVG.nvgText(ctx, x, y + getLineHeight(ctx), string);
	}

}
