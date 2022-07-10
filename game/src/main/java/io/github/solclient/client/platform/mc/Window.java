package io.github.solclient.client.platform.mc;

public interface Window {

	static int displayWidth() {
		throw new UnsupportedOperationException();
	}

	static int displayHeight() {
		throw new UnsupportedOperationException();
	}

	int getWidth();

	int getHeight();

	int getScaleFactor();

	int getScaledWidth();

	int getScaledHeight();

	double getScaledWidthD();

	double getScaledHeightD();

}
