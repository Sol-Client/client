package io.github.solclient.client.ui.screen;

import io.github.solclient.client.platform.mc.DrawableHelper;
import io.github.solclient.client.platform.mc.MinecraftClient;
import io.github.solclient.client.platform.mc.screen.ProxyScreen;
import io.github.solclient.client.platform.mc.screen.TitleScreen;
import io.github.solclient.client.platform.mc.text.Text;
import io.github.solclient.client.platform.mc.text.TextColour;

public class TestScreen extends ProxyScreen {

	private final TitleScreen titleScreen = MinecraftClient.getInstance().getTitleScreen();

	public TestScreen() {
		super(Text.literal("Test"));
	}

	@Override
	public void renderScreen(int mouseX, int mouseY, float tickDelta) {
		super.renderScreen(mouseX, mouseY, tickDelta);
		titleScreen.renderTitlePanorama(mouseX, mouseY, tickDelta);
		mc.getFont().render("Hello", 0, 0, -1);
		mc.getFont().renderWithShadow(
				Text.literal("red").style((style) -> style.withColour(TextColour.RED).withBold(true)), 20, 0, -1);
		DrawableHelper.fillRect(0, 0, 10, 10, 0xFFFF0000);
	}

	@Override
	public void tickScreen() {
		super.tickScreen();
		titleScreen.tickScreen();
	}

	@Override
	public void initScreen() {
		super.initScreen();
		titleScreen.update(mc, width, height);
	}

}
