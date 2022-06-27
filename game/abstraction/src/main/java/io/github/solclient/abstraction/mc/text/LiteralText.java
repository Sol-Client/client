package io.github.solclient.abstraction.mc.text;

import java.util.List;

import org.jetbrains.annotations.NotNull;

public interface LiteralText extends Text {

	static @NotNull LiteralText create(@NotNull String text) {
		throw new UnsupportedOperationException();
	}

	@NotNull String getText();
}
