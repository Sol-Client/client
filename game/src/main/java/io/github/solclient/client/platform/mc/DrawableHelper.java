package io.github.solclient.client.platform.mc;

public interface DrawableHelper {

	static void fillRect(int top, int left, int right, int bottom, int colour) {
		throw new UnsupportedOperationException();
	}

	static void fillGradientRect(int top, int left, int right, int bottom, int topColour, int bottomColour) {
		throw new UnsupportedOperationException();
	}

	static void strokeRect(int top, int left, int right, int bottom, int colour) {
		throw new UnsupportedOperationException();
	}

	static void fillTexturedRect(int x, int y, float u, float v, int width, int height, int textureWidth, int textureHeight) {
		throw new UnsupportedOperationException();
	}

	static void renderVerticalLine(int x, int startY, int endY, int colour) {
		throw new UnsupportedOperationException();
	}

}
