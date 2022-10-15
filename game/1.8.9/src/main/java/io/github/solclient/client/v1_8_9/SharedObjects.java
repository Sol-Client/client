package io.github.solclient.client.v1_8_9;

public class SharedObjects {

	/**
	 * A handy global drawable helper instance.
	 */
	public static final DrawableHelper HANDY_HELPER = new DrawableHelper();

	public static class DrawableHelper extends net.minecraft.client.gui.DrawableHelper {

		@Override
		public void fillGradient(int x1, int y1, int x2, int y2, int color1, int color2) {
			super.fillGradient(x1, y1, x2, y2, color1, color2);
		}

		@Override
		public void drawHorizontalLine(int x1, int x2, int y, int color) {
			super.drawHorizontalLine(x1, x2, y, color);
		}

		@Override
		public void drawVerticalLine(int x, int y1, int y2, int color) {
			super.drawVerticalLine(x, y1, y2, color);
		}

	}

}
