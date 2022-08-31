package io.github.solclient.client.platform.mc.text;

import org.jetbrains.annotations.NotNull;

import io.github.solclient.client.platform.VirtualEnum;
import org.jetbrains.annotations.Nullable;

/**
 * Legacy chat colour codes.
 */
public interface TextFormatting extends VirtualEnum {

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
