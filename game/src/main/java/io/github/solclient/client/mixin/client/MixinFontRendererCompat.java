package io.github.solclient.client.mixin.client;

import java.util.function.IntSupplier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import io.github.solclient.client.util.font.Font;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;

@Mixin(FontRenderer.class)
public abstract class MixinFontRendererCompat implements Font {

	@Override
	public int getHeight() {
		return FONT_HEIGHT;
	}

	@Override
	public int renderString(String text, float x, float y, int colour) {
		return withOffset(x, y, () -> drawString(text, (int) x, (int) y, colour));
	}

	private int withOffset(float x, float y, IntSupplier function) {
		float offsetX = x - ((float) Math.floor(x));
		float offsetY = y - ((float) Math.floor(y));

		if(offsetX != 0 || offsetY != 0) {
			GlStateManager.pushMatrix();
			GlStateManager.translate(offsetX, offsetY, 0);
		}

		int result = function.getAsInt();

		if(offsetX != 0 || offsetY != 0) {
			GlStateManager.popMatrix();
		}

		return result;
	}

	@Override
	public int renderStringWithShadow(String text, float x, float y, int colour) {
		return drawStringWithShadow(text, x, y, colour);
	}

	@Override
	public void renderCenteredString(String text, float x, float y, int color) {
		withOffset(x, y, () -> drawStringWithShadow(text, x - getStringWidth(text) / 2, y, color));
	}

	@Override
	public float getWidth(String text) {
		return (float) getStringWidth(text);
	}

	@Shadow
	protected abstract int getStringWidth(String text);

	@Shadow(prefix = "shadow$")
	public abstract int drawString(String text, int x, int y, int color);

	@Shadow(prefix = "shadow$")
	public abstract int drawStringWithShadow(String text, float x, float y, int color);

	@Shadow
	public int FONT_HEIGHT;

}
