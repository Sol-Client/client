package io.github.solclient.client.platform.mc.text;

import java.util.List;
import java.util.Objects;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import io.github.solclient.client.platform.Helper;

public interface Text extends Cloneable {

	static @NotNull MutableText literal(@NotNull String text) {
		throw new UnsupportedOperationException();
	}

	static @NotNull MutableText format(@NotNull String fmt, Object... args) {
		throw new UnsupportedOperationException();
	}

	static @NotNull MutableText translation(String key, Object... args) {
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

	@NotNull List<Text> getExtraText();

	@NotNull Text clone();

	@NotNull String getPlain();

	@NotNull Style getStyle();

	@NotNull
	String getLegacy();

}
