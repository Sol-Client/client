package io.github.solclient.abstraction.mc.text;

public interface TextColour {

	TextColour BLACK = null;
	TextColour DARK_BLUE = null;
	TextColour DARK_GREEN = null;
	TextColour DARK_AQUA = null;
	TextColour DARK_RED = null;
	TextColour DARK_PURPLE = null;
	TextColour GOLD = null;
	TextColour GREY = null;
	TextColour DARK_GREY = null;
	TextColour BLUE = null;
	TextColour GREEN = null;
	TextColour AQUA = null;
	TextColour RED = null;
	TextColour LIGHT_PURPLE = null;
	TextColour YELLOW = null;
	TextColour WHITE = null;

	static TextColour create(int rgb) {
		throw new UnsupportedOperationException();
	}

	static TextColour create(int r, int g, int b) {
		throw new UnsupportedOperationException();
	}

	int getColour();

}
