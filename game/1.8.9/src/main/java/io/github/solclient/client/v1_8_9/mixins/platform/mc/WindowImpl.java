package io.github.solclient.client.v1_8_9.mixins.platform.mc;

import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import io.github.solclient.client.platform.mc.Window;

@Mixin(net.minecraft.client.util.Window.class)
@Implements(@Interface(iface = Window.class, prefix = "platform$"))
public class WindowImpl {

	@Shadow
	private int width, height, scaleFactor;
	@Shadow
	private double scaledWidth, scaledHeight;

	public int platform$getWidth() {
		return width;
	}

	public int platform$getHeight() {
		return height;
	}

	public int platform$getScaleFactor() {
		return scaleFactor;
	}

	public int platform$getScaledWidth() {
		return (int) scaledWidth;
	}

	public int platform$getScaledHeight() {
		return (int) scaledHeight;
	}

	public double platform$getScaledWidthD() {
		return scaledWidth;
	}

	public double platform$getScaledHeightD() {
		return scaledHeight;
	}

}
