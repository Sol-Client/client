package io.github.solclient.client.platform.mc.text;

import org.jetbrains.annotations.NotNull;

public interface Font {

	int render(@NotNull String text, int x, int y, int rgb);

	int render(@NotNull String text, int x, int y, int rgb, boolean shadow);

	int renderWithShadow(@NotNull String text, int x, int y, int rgb);

	int render(@NotNull Text text, int x, int y, int rgb);

	int render(@NotNull Text text, int x, int y, int rgb, boolean shadow);

	int renderWithShadow(@NotNull Text text, int x, int y, int rgb);

	int getCharacterWidth(char character);

	int getTextWidth(@NotNull String text);

	int getTextWidth(@NotNull Text text);

	int getHeight();

}
