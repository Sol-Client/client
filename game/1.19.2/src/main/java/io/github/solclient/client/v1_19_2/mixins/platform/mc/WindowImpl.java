package io.github.solclient.client.v1_19_2.mixins.platform.mc;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import io.github.solclient.client.platform.mc.Window;

@Mixin(net.minecraft.client.util.Window.class)
public class WindowImpl implements Window {

	@Shadow
	private int width, height;
	@Shadow
	private double scaleFactor;
	@Shadow
	private int scaledWidth, scaledHeight;

	@Override
	public int width() {
		return width;
	}

	@Override
	public int height() {
		return height;
	}

	@Override
	public int scaleFactor() {
		return (int) scaleFactor;
	}

	@Override
	public int scaledWidth() {
		return scaledWidth;
	}

	@Override
	public int scaledHeight() {
		return scaledHeight;
	}

	@Override
	public double scaledWidthD() {
		return scaledWidth;
	}

	@Override
	public double scaledHeightD() {
		return scaledHeight;
	}

}
