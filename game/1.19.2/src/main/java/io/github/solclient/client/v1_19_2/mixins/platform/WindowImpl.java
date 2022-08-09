package io.github.solclient.client.v1_19_2.mixins.platform;

import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import io.github.solclient.client.platform.mc.Window;

@Mixin(net.minecraft.client.util.Window.class)
@Implements(@Interface(iface = Window.class, prefix = "platform$"))
public class WindowImpl {

	@Shadow
	private int width, height;
	@Shadow
	private double scaleFactor;
	@Shadow
	private int scaledWidth, scaledHeight;

	public int platform$getWidth() {
		return width;
	}

	public int platform$getHeight() {
		return height;
	}

	public int platform$getScaleFactor() {
		return (int) scaleFactor;
	}

	public int platform$getScaledWidth() {
		return scaledWidth;
	}

	public int platform$getScaledHeight() {
		return scaledHeight;
	}

	public double platform$getScaledWidthD() {
		return (double) scaledWidth;
	}

	public double platform$getScaledHeightD() {
		return (double) scaledHeight;
	}

}
