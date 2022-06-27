package io.github.solclient.abstraction.mc.text;

import java.util.List;
import java.util.function.Consumer;

import org.jetbrains.annotations.NotNull;

public interface Text extends Cloneable {

	@NotNull List<Text> getSiblings();

	void append(@NotNull Text text);

	@NotNull Text clone();

	@NotNull String getPlain();

	/**
	 * @return The text, with legacy formatting codes.
	 */
	@NotNull String getLegacyText();

	@NotNull Style getStyle();

	@NotNull void setStyle(@NotNull Style style);

	@NotNull Text withStyle(Consumer<Style> styler);

	@NotNull Text withoutColour();

}
