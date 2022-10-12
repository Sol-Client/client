package io.github.solclient.client.v1_19_2.mixins.platform.mc.text;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.*;

import io.github.solclient.client.platform.mc.text.*;
import io.github.solclient.client.v1_19_2.SharedObjects;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;

@Mixin(TextRenderer.class)
@Implements(@Interface(iface = Font.class, prefix = "platform$"))
public abstract class FontImpl {

	public int platform$render(@NotNull String text, int x, int y, int rgb) {
		return draw(SharedObjects.primary2dMatrixStack, text, x, y, rgb);
	}

	public int platform$render(@NotNull String text, int x, int y, int rgb, boolean shadow) {
		if(shadow) {
			return drawWithShadow(SharedObjects.primary2dMatrixStack, text, x, y, rgb);
		}
		return draw(SharedObjects.primary2dMatrixStack, text, x, y, rgb);
	}

	public int platform$renderWithShadow(@NotNull String text, int x, int y, int rgb) {
		return drawWithShadow(SharedObjects.primary2dMatrixStack, text, x, y, rgb);
	}

	@Shadow
	public abstract int draw(MatrixStack stack, String text, float x, float y, int color);

	@Shadow
	public abstract int drawWithShadow(MatrixStack stack, String text, float x, float y, int color);

	public int platform$render(@NotNull Text text, int x, int y, int rgb) {
		return draw(SharedObjects.primary2dMatrixStack, (net.minecraft.text.Text) text, x, y, rgb);
	}

	public int platform$render(@NotNull Text text, int x, int y, int rgb, boolean shadow) {
		if(shadow) {
			return drawWithShadow(SharedObjects.primary2dMatrixStack, (net.minecraft.text.Text) text, x, y, rgb);
		}
		return draw(SharedObjects.primary2dMatrixStack, (net.minecraft.text.Text) text, x, y, rgb);
	}

	public int platform$renderWithShadow(@NotNull Text text, int x, int y, int rgb) {
		return drawWithShadow(SharedObjects.primary2dMatrixStack, (net.minecraft.text.Text) text, x, y, rgb);
	}

	@Shadow
	public abstract int draw(MatrixStack stack, net.minecraft.text.Text text, float x, float y, int color);

	@Shadow
	public abstract int drawWithShadow(MatrixStack stack, net.minecraft.text.Text text, float x, float y, int color);

	public int platform$getCharacterWidth(char character) {
		return getWidth(Character.toString(character));
	}

	public int platform$getTextWidth(@NotNull String text) {
		return getWidth(text);
	}

	public int platform$getTextWidth(@NotNull Text text) {
		return getWidth(((net.minecraft.text.Text) text).asOrderedText());
	}

	@Shadow
	public abstract int getWidth(String text);

	@Shadow
	public abstract int getWidth(OrderedText text);

	public int platform$getHeight() {
		return fontHeight;
	}

	@Shadow
	public @Final int fontHeight;

}
