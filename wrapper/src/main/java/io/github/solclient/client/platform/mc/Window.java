package io.github.solclient.client.platform.mc;

public interface Window {

	static int displayWidth() {
		throw new UnsupportedOperationException();
	}

	static int displayHeight() {
		throw new UnsupportedOperationException();
	}

	int width();

	int height();

	int scaleFactor();

	int scaledWidth();

	int scaledHeight();

	double scaledWidthD();

	double scaledHeightD();

}
