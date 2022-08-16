package io.github.solclient.client.platform.mc.screen;

import io.github.solclient.client.platform.mc.MinecraftClient;
import io.github.solclient.client.platform.mc.text.Font;
import io.github.solclient.client.platform.mc.text.Text;

public class ProxyScreen implements Screen {

	protected final int width, height;
	protected final MinecraftClient mc;
	protected final Font font;

	public ProxyScreen(Text title) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void renderScreen(int mouseX, int mouseY, float tickDelta) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void keyDown(char character, int key) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void mouseDown(int x, int y, int button) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void mouseUp(int x, int y, int button) {
		throw new UnsupportedOperationException();
	}

	public void scroll(int by) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void update(MinecraftClient mc, int width, int height) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void initScreen() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void onClose() {
		throw new UnsupportedOperationException();
	}

	protected void renderBackground() {
		throw new UnsupportedOperationException();
	}

	protected void renderTranslucentBackground() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void tickScreen() {
		throw new UnsupportedOperationException();
	}

	public boolean shouldPauseGame() {
		return true;
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}

}
