package io.github.solclient.client.ui.screen;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.Window;

public class SplashScreen {

	private static final int FG = 0xFFDF242F;
	private static final int BG = 0xFF000000;

	public static final SplashScreen INSTANCE = new SplashScreen();

	private static final int STAGES = 18;

	private final MinecraftClient mc = MinecraftClient.getInstance();
	private int stage;

	public void reset() {
		stage = 0;
	}

	public void draw() {
		if (stage > STAGES) {
			throw new IndexOutOfBoundsException(Integer.toString(stage));
		}

		Window window = new Window(mc);
		int factor = window.getScaleFactor();

		DrawableHelper.fill(0, window.getHeight() * factor - 30, window.getWidth() * factor,
				window.getHeight() * factor, BG);
		DrawableHelper.fill(0, window.getHeight() * factor - 30, window.getWidth() * factor / STAGES * stage,
				window.getHeight() * factor, FG);
		stage++;
	}

}
