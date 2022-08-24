package io.github.solclient.client.platform.mc.text;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import io.github.solclient.client.platform.Helper;

public interface Text extends Cloneable {

	static @NotNull Text literal(@NotNull String text) {
		throw new UnsupportedOperationException();
	}

	static @NotNull Text format(@NotNull String fmt, Object... args) {
		throw new UnsupportedOperationException();
	}

	static @NotNull Text translation(String key, Object... args) {
		throw new UnsupportedOperationException();
	}

	@Helper
	static boolean plainEquals(@Nullable Text text, @NotNull String to) {
		Objects.requireNonNull(to);

		if(text == null) {
			return false;
		}

		return text.getPlain().equals(to);
	}

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
