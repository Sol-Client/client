package io.github.solclient.client.v1_8_9.mixins.platform.mc;

import com.mojang.blaze3d.platform.GlStateManager;
import io.github.solclient.client.platform.mc.DrawableHelper;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(DrawableHelper.class)
public class DrawableHelperImpl {

	@Overwrite(remap = false)
	public static void fillRect(int left, int top, int right, int bottom, int colour) {
		fillRect((float) left, top, right, bottom, colour);
	}

	@Overwrite(remap = false)
	public static void fillRect(float left, float top, float right, float bottom, int colour) {
		{
			float tmp;

			if(left < right) {
				tmp = left;
				left = right;
				right = tmp;
			}

			if(top < bottom) {
				tmp = top;
				top = bottom;
				bottom = tmp;
			}
		}

		float r = (colour >> 16 & 255) / 255F,
				g = (colour >> 8 & 255) / 255F,
				b = (colour & 255) / 255F,
				a = (colour >> 24 & 255) / 255F;
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffers = tessellator.getBuffer();
		GlStateManager.enableBlend();
		GlStateManager.disableTexture();
		GlStateManager.blendFuncSeparate(770, 771, 1, 0);
		GlStateManager.color4f(r, g, b, a);
		buffers.begin(7, VertexFormats.POSITION);
		buffers.vertex(left, bottom, 0).next();
		buffers.vertex(right, bottom, 0).next();
		buffers.vertex(right, top, 0).next();
		buffers.vertex(left, top, 0).next();
		tessellator.draw();
		GlStateManager.enableTexture();
		GlStateManager.disableBlend();
	}

	@Overwrite(remap = false)
	public static void fillGradientRect(int left, int top, int right, int bottom, int topColour, int bottomColour) {
		{
			int tmp;

			if(left < right) {
				tmp = left;
				left = right;
				right = tmp;
			}

			if(top < bottom) {
				tmp = top;
				top = bottom;
				bottom = tmp;
			}
		}

		float topR = (topColour >> 16 & 255) / 255F,
				topG = (topColour >> 8 & 255) / 255F,
				topB = (topColour & 255) / 255F,
				topA = (topColour >> 24 & 255) / 255F;

		float bottomR = (bottomColour >> 16 & 255) / 255F,
				bottomG = (bottomColour >> 8 & 255) / 255F,
				bottomB = (bottomColour & 255) / 255F,
				bottomA = (bottomColour >> 24 & 255) / 255F;

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffers = tessellator.getBuffer();
		GlStateManager.disableTexture();
		GlStateManager.enableBlend();
		GlStateManager.disableAlphaTest();
		GlStateManager.blendFuncSeparate(770, 771, 1, 0);
		GlStateManager.shadeModel(7425);
		buffers.begin(7, VertexFormats.POSITION_COLOR);
		buffers.vertex(left, bottom, 0).color(bottomR, bottomG, bottomB, bottomA).next();
		buffers.vertex(right, bottom, 0).color(bottomR, bottomG, bottomB, bottomA).next();
		buffers.vertex(right, top, 0).color(topR, topG, topB, topA).next();
		buffers.vertex(left, top, 0).color(topR, topG, topB, topA).next();
		tessellator.draw();
		GlStateManager.shadeModel(7424);
		GlStateManager.disableBlend();
		GlStateManager.enableAlphaTest();
		GlStateManager.enableTexture();
	}

	@Overwrite(remap = false)
	public static void strokeRect(int left, int top, int right, int bottom, int colour) {
		renderHorizontalLine(left, right - 1, top, colour);
		renderHorizontalLine(left, right - 1, bottom -1, colour);
		renderVerticalLine(left, top, bottom - 1, colour);
		renderVerticalLine(right - 1, top, bottom - 1, colour);
	}

	@Overwrite(remap = false)
	public static void fillTexturedRect(int x, int y, float u, float v, int width, int height, int textureWidth, int textureHeight) {
		net.minecraft.client.gui.DrawableHelper.drawTexture(x, y, u, v, width, height, textureWidth, textureHeight);
	}

	@Overwrite(remap = false)
	public static void renderHorizontalLine(int startX, int endX, int y, int colour) {
		if (endX < startX) {
			int tmp = startX;
			startX = endX;
			endX = tmp;
		}

		fillRect(startX, y, endX + 1, y + 1, colour);
	}

	@Overwrite(remap = false)
	public static void renderVerticalLine(int x, int startY, int endY, int colour) {
		if(endY < startY) {
			int tmp = startY;
			startY = endY;
			endY = tmp;
		}

		fillRect(x, startY + 1, x + 1, endY, colour);
	}

}
