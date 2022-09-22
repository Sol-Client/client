package io.github.solclient.client.v1_8_9.mixins.platform.mc.text;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import io.github.solclient.client.platform.mc.text.Font;
import io.github.solclient.client.platform.mc.text.Text;
import net.minecraft.client.font.TextRenderer;

@Mixin(TextRenderer.class)
@Implements(@Interface(iface = Font.class, prefix = "platform$"))
public abstract class FontImpl {

	public int platform$render(@NotNull String text, int x, int y, int rgb) {
		return draw(text, x, y, rgb, false);
	}

	public int platform$render(@NotNull String text, int x, int y, int rgb, boolean shadow) {
		return draw(text, x, y, rgb, shadow);
	}

	public int platform$renderWithShadow(@NotNull String text, int x, int y, int rgb) {
		return draw(text, x, y, rgb, true);
	}

	public int platform$render(@NotNull Text text, int x, int y, int rgb) {
		return draw(text.getLegacy(), x, y, rgb, false);
	}

	public int platform$render(@NotNull Text text, int x, int y, int rgb, boolean shadow) {
		return draw(text.getLegacy(), x, y, rgb, shadow);
	}

	public int platform$renderWithShadow(@NotNull Text text, int x, int y, int rgb) {
		return draw(text.getLegacy(), x, y, rgb, true);
	}

	@Shadow
	public abstract int draw(String text, float x, float y, int color, boolean shadow);

	public int platform$getCharacterWidth(char character) {
		return getCharWidth(character);
	}

	@Shadow
	public abstract int getCharWidth(char character);

	public int platform$getTextWidth(@NotNull String text) {
		return getStringWidth(text);
	}

	public int platform$getTextWidth(@NotNull Text text) {
		return getStringWidth(text.getLegacy());
	}

	@Shadow
	public abstract int getStringWidth(String text);

	public int platform$getHeight() {
		return fontHeight;
	}

	@Shadow
	public int fontHeight;

}
