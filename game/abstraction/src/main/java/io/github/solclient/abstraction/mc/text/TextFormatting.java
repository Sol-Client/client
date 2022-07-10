package io.github.solclient.abstraction.mc.text;

import org.jetbrains.annotations.NotNull;

/**
 * Legacy chat colour codes.
 */
public interface TextFormatting {

	TextFormatting BLACK = null,
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
			WHITE = null,
			BOLD = null,
			ITALIC = null,
			STRIKETHROUGH = null,
			OBFUSCATED = null;

	static String strip(String message) {
		throw new UnsupportedOperationException();
	}

	char getChar();

	/**
	 * @return §0123456789abcdef
	 */
	@Override
	@NotNull String toString();

}
