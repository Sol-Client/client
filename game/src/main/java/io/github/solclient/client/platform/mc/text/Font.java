package io.github.solclient.client.platform.mc.text;

import org.jetbrains.annotations.NotNull;

public interface Font {

	int render(@NotNull String text, int x, int y, int rgb);

	int render(@NotNull String text, int x, int y, int rgb, boolean shadow);

	int renderWithShadow(@NotNull String text, int x, int y, int rgb);

	int render(@NotNull Text text, int x, int y, int rgb);

	int render(@NotNull Text text, int x, int y, int rgb, boolean shadow);

	int renderWithShadow(@NotNull Text text, int x, int y, int rgb);

	default int render(@NotNull OrderedText text, int x, int y, int rgb) {
		return render((Text) text, x, y, rgb);
	}

	default int render(@NotNull OrderedText text, int x, int y, int rgb, boolean shadow) {
		return render((Text) text, x, y, rgb, shadow);
	}

	default int renderWithShadow(@NotNull OrderedText text, int x, int y, int rgb) {
		return renderWithShadow((Text) text, x, y, rgb);
	}

	int getCharacterWidth(char character);

	int getTextWidth(@NotNull String text);

	int getTextWidth(@NotNull Text text);

	default int getTextWidth(@NotNull OrderedText text) {
		return getTextWidth((Text) text);
	}

	int getHeight();

}
