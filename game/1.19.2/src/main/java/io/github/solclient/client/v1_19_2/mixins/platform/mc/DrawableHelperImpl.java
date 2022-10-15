package io.github.solclient.client.v1_19_2.mixins.platform.mc;

import org.spongepowered.asm.mixin.*;

import com.mojang.blaze3d.systems.RenderSystem;

import io.github.solclient.client.platform.mc.DrawableHelper;
import io.github.solclient.client.v1_19_2.SharedObjects;
import net.minecraft.client.render.*;
import net.minecraft.util.math.Matrix4f;

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

		Matrix4f matrix = SharedObjects.primary2dMatrixStack.peek().getPositionMatrix();

		float r = (colour >> 16 & 255) / 255F,
				g = (colour >> 8 & 255) / 255F,
				b = (colour & 255) / 255F,
				a = (colour >> 24 & 255) / 255F;

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffers = tessellator.getBuffer();
		RenderSystem.enableBlend();
		RenderSystem.disableTexture();
		RenderSystem.defaultBlendFunc();
		RenderSystem.setShader(GameRenderer::getPositionColorShader);
		buffers.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
		buffers.vertex(matrix, left, bottom, 0).color(r, g, b, a).next();
		buffers.vertex(matrix, right, bottom, 0).color(r, g, b, a).next();
		buffers.vertex(matrix, right, top, 0).color(r, g, b, a).next();
		buffers.vertex(matrix, left, top, 0).color(r, g, b, a).next();
		BufferRenderer.drawWithShader(buffers.end());
		RenderSystem.enableTexture();
		RenderSystem.disableBlend();
	}

	@Overwrite(remap = false)
	public static void fillGradientRect(int left, int top, int right, int bottom, int topColour, int bottomColour) {
		SharedObjects.HANDY_HELPER.fillGradient(SharedObjects.primary2dMatrixStack, left, top, right, bottom, topColour, bottomColour);
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
		net.minecraft.client.gui.DrawableHelper.drawTexture(SharedObjects.primary2dMatrixStack, x, y, u, v, width, height, textureWidth, textureHeight);
	}

	@Overwrite(remap = false)
	public static void renderHorizontalLine(int startX, int endX, int y, int colour) {
		SharedObjects.HANDY_HELPER.drawHorizontalLine(SharedObjects.primary2dMatrixStack, startX, endX, y, colour);
	}

	@Overwrite(remap = false)
	public static void renderVerticalLine(int x, int startY, int endY, int colour) {
		SharedObjects.HANDY_HELPER.drawVerticalLine(SharedObjects.primary2dMatrixStack, x, startY, endY, colour);
	}

}
