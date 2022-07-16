//package io.github.solclient.client.v1_8_9.mixins.platform;
//
//import org.lwjgl.opengl.Display;
//
//import io.github.solclient.client.platform.mc.Window;
//import lombok.Setter;
//import net.minecraft.client.gui.ScaledResolution;
//
//public class WindowImpl implements Window {
//
//	@Setter
//	private ScaledResolution resolution;
//
//	@Override
//	public int getWidth() {
//		return Display.getWidth();
//	}
//
//	@Override
//	public int getHeight() {
//		return Display.getHeight();
//	}
//
//	@Override
//	public int getScaleFactor() {
//		return resolution.getScaleFactor();
//	}
//
//	@Override
//	public int getScaledWidth() {
//		return resolution.getScaledWidth();
//	}
//
//	@Override
//	public int getScaledHeight() {
//		return resolution.getScaledHeight();
//	}
//
//	@Override
//	public double getScaledWidthD() {
//		return resolution.getScaledWidth_double();
//	}
//
//	@Override
//	public double getScaledHeightD() {
//		return resolution.getScaledHeight_double();
//	}
//
//}
