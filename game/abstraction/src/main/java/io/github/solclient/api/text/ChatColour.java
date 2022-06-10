package io.github.solclient.api.text;

public interface ChatColour {

	ChatColour BLACK = null;
	ChatColour DARK_BLUE = null;
	ChatColour DARK_GREEN = null;
	ChatColour DARK_AQUA = null;
	ChatColour DARK_RED = null;
	ChatColour DARK_PURPLE = null;
	ChatColour GOLD = null;
	ChatColour GRAY = null;
	ChatColour DARK_GRAY = null;
	ChatColour BLUE = null;
	ChatColour GREEN = null;
	ChatColour AQUA = null;
	ChatColour RED = null;
	ChatColour LIGHT_PURPLE = null;
	ChatColour YELLOW = null;
	ChatColour WHITE = null;

	static ChatColour create(int rgb) {
		throw new UnsupportedOperationException();
	}

	static ChatColour create(int r, int g, int b) {
		throw new UnsupportedOperationException();
	}

	int getColour();

	char getChar();

}
