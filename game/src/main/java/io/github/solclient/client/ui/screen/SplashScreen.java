package io.github.solclient.client.ui.screen;

import io.github.solclient.client.util.data.Colour;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;

public class SplashScreen {

	private static final int FG = 0xFFDF242F;
	private static final int BG = 0xFF000000;

	public static final SplashScreen INSTANCE = new SplashScreen();
	private Minecraft mc = Minecraft.getMinecraft();
	private int stage;
	private int stages = 18;

	public void reset() {
		stage = 0;
	}

	public void setStages(int stages) {
		this.stages = stages;
	}

	public void draw() {
		if(stage > stages) {
			throw new IndexOutOfBoundsException(Integer.toString(stage));
		}

		ScaledResolution resolution = new ScaledResolution(mc);
		int factor = resolution.getScaleFactor();

		Gui.drawRect(0, resolution.getScaledHeight() * factor - 30, resolution.getScaledWidth() * factor,
				resolution.getScaledHeight() * factor, BG);
		Gui.drawRect(0, resolution.getScaledHeight() * factor - 30,
				resolution.getScaledWidth() * factor / stages * stage, resolution.getScaledHeight() * factor, FG);
		stage++;
	}

}
