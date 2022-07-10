package io.github.solclient.client.platform.mc.text;

public interface TextColour {

	TextColour BLACK = null,
			DARK_BLUE = null,
			DARK_GREEN = null,
			DARK_AQUA = null,
			DARK_RED = null,
			DARK_PURPLE = null,
			GOLD = null,
			GREY = null,
			DARK_GREY = null,
			BLUE = null,
			GREEN = null,
			AQUA = null,
			RED = null,
			LIGHT_PURPLE = null,
			YELLOW = null,
			WHITE = null;

	static TextColour create(int rgb) {
		throw new UnsupportedOperationException();
	}

	static TextColour create(int r, int g, int b) {
		throw new UnsupportedOperationException();
	}

	int getColour();

}
