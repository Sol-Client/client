package io.github.solclient.abstraction.mc.text;

import org.jetbrains.annotations.NotNull;

/**
 * Legacy chat colour codes.
 */
public interface TextFormatting {

	TextFormatting BLACK = null;
	TextFormatting DARK_BLUE = null;
	TextFormatting DARK_GREEN = null;
	TextFormatting DARK_AQUA = null;
	TextFormatting DARK_RED = null;
	TextFormatting DARK_PURPLE = null;
	TextFormatting GOLD = null;
	TextFormatting GREY = null;
	TextFormatting DARK_GREY = null;
	TextFormatting BLUE = null;
	TextFormatting GREEN = null;
	TextFormatting AQUA = null;
	TextFormatting RED = null;
	TextFormatting LIGHT_PURPLE = null;
	TextFormatting YELLOW = null;
	TextFormatting WHITE = null;
	TextFormatting BOLD = null;
	TextFormatting ITALIC = null;
	TextFormatting STRIKETHROUGH = null;
	TextFormatting OBFUSCATED = null;

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
