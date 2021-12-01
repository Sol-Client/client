package me.mcblueparrot.client.ui.screen;

import me.mcblueparrot.client.util.data.Colour;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;

public class SplashScreen {

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
		int bg = new Colour(0, 0, 0).getValue();
		int fg = new Colour(255, 50, 50).getValue();
		int factor = resolution.getScaleFactor();
		Gui.drawRect(0, resolution.getScaledHeight() * factor - 30, resolution.getScaledWidth() * factor,
				resolution.getScaledHeight() * factor, bg);
		Gui.drawRect(0, resolution.getScaledHeight() * factor - 30,
				resolution.getScaledWidth() * factor / stages * stage, resolution.getScaledHeight() * factor, fg);
		stage++;
	}

}
