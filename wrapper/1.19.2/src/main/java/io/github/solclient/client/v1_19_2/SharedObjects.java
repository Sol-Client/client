package io.github.solclient.client.v1_19_2;

import net.minecraft.client.util.math.MatrixStack;

/**
 * Collection of objects that cannot be passed through arguments.
 */
public class SharedObjects {

	/**
	 * The primary matrix stack used for GUI rendering.
	 */
	public static MatrixStack primary2dMatrixStack;

	/**
	 * A handy global drawable helper instance.
	 */
	public static final DrawableHelper HANDY_HELPER = new DrawableHelper();

	public static class DrawableHelper extends net.minecraft.client.gui.DrawableHelper {

		@Override
		public void fillGradient(MatrixStack matrices, int startX, int startY, int endX, int endY, int colorStart,
				int colorEnd) {
			super.fillGradient(matrices, startX, startY, endX, endY, colorStart, colorEnd);
		}

		@Override
		public void drawHorizontalLine(MatrixStack matrices, int x1, int x2, int y, int color) {
			super.drawHorizontalLine(matrices, x1, x2, y, color);
		}

		@Override
		public void drawVerticalLine(MatrixStack matrices, int x, int y1, int y2, int color) {
			super.drawVerticalLine(matrices, x, y1, y2, color);
		}

	}

}
