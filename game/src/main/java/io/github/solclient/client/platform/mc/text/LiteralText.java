package io.github.solclient.client.platform.mc.text;

import org.jetbrains.annotations.NotNull;

import io.github.solclient.client.platform.Helper;

public interface LiteralText extends Text {

	static @NotNull LiteralText create(@NotNull String text) {
		throw new UnsupportedOperationException();
	}

	static @NotNull LiteralText format(@NotNull String fmt, Object... args) {
		throw new UnsupportedOperationException();
	}

	@NotNull String getText();

}
