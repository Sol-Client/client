package io.github.solclient.client.v1_8_9.mixins.platform.mc;

import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import io.github.solclient.client.platform.mc.Window;

@Mixin(net.minecraft.client.util.Window.class)
public class WindowImpl implements Window {

	@Shadow
	private int width, height, scaleFactor;
	@Shadow
	private double scaledWidth, scaledHeight;

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
		return scaleFactor;
	}

	@Override
	public int scaledWidth() {
		return (int) scaledWidth;
	}

	@Override
	public int scaledHeight() {
		return (int) scaledHeight;
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
