package io.github.solclient.client.platform.mc.text;

import org.jetbrains.annotations.*;

import io.github.solclient.client.platform.VirtualEnum;

/**
 * Legacy chat colour codes.
 */
public interface TextFormatting extends VirtualEnum {

	TextFormatting BLACK = get("BLACK"),
			DARK_BLUE = get("DARK_BLUE"),
			DARK_GREEN = get("DARK_GREEN"),
			DARK_AQUA = get("DARK_AQUA"),
			DARK_RED = get("DARK_RED"),
			DARK_PURPLE = get("DARK_PURPLE"),
			GOLD = get("GOLD"),
			GREY = get("GRAY"),
			DARK_GREY = get("DARK_GRAY"),
			BLUE = get("BLUE"),
			GREEN = get("GREEN"),
			AQUA = get("AQUA"),
			RED = get("RED"),
			LIGHT_PURPLE = get("LIGHT_PURPLE"),
			YELLOW = get("YELLOW"),
			WHITE = get("WHITE"),
			BOLD = get("BOLD"),
			ITALIC = get("ITALIC"),
			STRIKETHROUGH = get("STRIKETHROUGH"),
			OBFUSCATED = get("OBFUSCATED");

	static @NotNull TextFormatting get(@NotNull String name) {
		throw new UnsupportedOperationException();
	}

	static @NotNull String strip(@NotNull String message) {
		throw new UnsupportedOperationException();
	}

	/**
	 * @return §0123456789abcdef
	 */
	@Override
	@NotNull String toString();

	@Nullable TextColour getColour();

}
