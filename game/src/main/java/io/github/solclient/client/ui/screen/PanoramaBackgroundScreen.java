package io.github.solclient.client.ui.screen;

import java.io.IOException;

import io.github.solclient.client.Client;
import io.github.solclient.client.platform.mc.MinecraftClient;
import io.github.solclient.client.platform.mc.screen.TitleScreen;
import io.github.solclient.client.platform.mc.text.Text;
import io.github.solclient.client.ui.component.Component;
import io.github.solclient.client.ui.component.Screen;

public abstract class PanoramaBackgroundScreen extends Screen {

	private TitleScreen titleScreen = MinecraftClient.getInstance().getMainMenu();

	public PanoramaBackgroundScreen(Text title, Component root) {
		super(title, root);
		background = false;
	}

	@Override
	public void update(MinecraftClient mc, int width, int height) {
		super.update(mc, width, height);

		if(!mc.hasLevel()) {
			titleScreen.update(mc, width, height);
		}
	}

	@Override
	public void tickScreen() {
		super.tickScreen();
		titleScreen.tickScreen();
	}

	protected void renderPanorama(int x, int y, float tickDelta) {
		titleScreen.renderPanorama(x, y, tickDelta);
	}

}
