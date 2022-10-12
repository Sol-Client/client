package io.github.solclient.client.util;

import java.io.*;
import java.nio.*;

import org.jetbrains.annotations.NotNull;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.stb.*;
import org.lwjgl.system.*;

import io.github.solclient.client.platform.mc.*;
import io.github.solclient.client.platform.mc.render.GlStateManager;
import io.github.solclient.client.platform.mc.text.*;

// TODO stuff
// Roughly based around
// https://github.com/LWJGL/lwjgl3/blob/master/modules/samples/src/test/java/org/lwjgl/demo/stb/Truetype.java
public class TrueTypeFont implements Font, Closeable {

	private final ByteBuffer buffer;
	private final int textureId;
	private final int ascent, descent, lineGap;

	public TrueTypeFont(@NotNull InputStream in, float size) throws IOException, IllegalStateException {
		buffer = Utils.mallocAndRead(in);

		STBTTFontinfo info = STBTTFontinfo.create();
		if(!STBTruetype.stbtt_InitFont(info, buffer)) {
			throw new IllegalStateException("stbtt_InitFont returned false");
		}

		try(MemoryStack stack = MemoryStack.stackPush()) {
			IntBuffer ascentBuffer = stack.mallocInt(1),
					descentBuffer = stack.mallocInt(1),
					lineGapBuffer = stack.mallocInt(1);
			STBTruetype.stbtt_GetFontVMetrics(info, ascentBuffer, descentBuffer, lineGapBuffer);
			ascent = ascentBuffer.get(0);
			descent = descentBuffer.get(0);
			lineGap = lineGapBuffer.get(0);
		}

		textureId = GL11.glGenTextures();

		int bitmapWidth = 512, bitmapHeight = 512;
		int scale = MinecraftClient.getInstance().getWindow().scaleFactor();
		bitmapWidth *= scale;
		bitmapHeight *= scale;

		ByteBuffer stbitmap = MemoryUtil.memAlloc(bitmapWidth * bitmapHeight);
		ByteBuffer bitmap = BufferUtils.createByteBuffer(stbitmap.capacity() * 4);

		// convert from {alpha} to {255, 255, 255, alpha}

		STBTTBakedChar.Buffer charData = STBTTBakedChar.create(96);
		STBTruetype.stbtt_BakeFontBitmap(buffer, size * scale, stbitmap, bitmapWidth, bitmapHeight, ' ', charData);

		while(stbitmap.hasRemaining()) {
			byte bite = stbitmap.get();
			bitmap.put(Byte.MAX_VALUE);
			bitmap.put(Byte.MAX_VALUE);
			bitmap.put(Byte.MAX_VALUE);
			bitmap.put(bite);
		}

		// very important
		// please don't remove this
		// removing this could cause big problems
		stbitmap.rewind();
		bitmap.rewind();

		MemoryUtil.memFree(stbitmap);

//		STBTruetype.stbtt_GetBakedQuad(charData, bitmapWidth, bitmapHeight, scale, null, null, null, false);

		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, bitmapWidth, bitmapHeight, 0, GL11.GL_RGBA, GL11.GL_BYTE, bitmap);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
	}

	@Override
	public int render(@NotNull String text, int x, int y, int rgb) {
		GlStateManager.enableTexture2d();
		GlStateManager.enableBlend();
		GlStateManager.blendFunction(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GlStateManager.bindTexture(textureId);
		DrawableHelper.fillTexturedRect(x, y, 0, 0, 512, 512, 512, 512);
		return 0;
	}

	@Override
	public int render(@NotNull String text, int x, int y, int rgb, boolean shadow) {
		return render(text, x, y, rgb);
	}

	@Override
	public int renderWithShadow(@NotNull String text, int x, int y, int rgb) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int render(@NotNull Text text, int x, int y, int rgb) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int render(@NotNull Text text, int x, int y, int rgb, boolean shadow) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int renderWithShadow(@NotNull Text text, int x, int y, int rgb) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getCharacterWidth(char character) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getTextWidth(@NotNull String text) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getTextWidth(@NotNull Text text) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getHeight() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void close() throws IOException {
		MemoryUtil.memFree(buffer);
		GL11.glDeleteTextures(textureId);
	}

}
