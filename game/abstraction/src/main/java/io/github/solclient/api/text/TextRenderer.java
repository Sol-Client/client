package io.github.solclient.api.text;

import java.io.InputStream;

import org.jetbrains.annotations.NotNull;

public interface TextRenderer {

	static @NotNull TextRenderer createTrueType(@NotNull InputStream in) {
		throw new UnsupportedOperationException();
	}

	int render(@NotNull String text, int x, int y, int rgb);

	int render(@NotNull String text, int x, int y, int rgb, boolean shadow);

	int renderWithShadow(@NotNull String text, int x, int y, int rgb);

	int getWidth(@NotNull String text);

}
