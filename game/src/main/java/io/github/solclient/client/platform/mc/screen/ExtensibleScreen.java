package io.github.solclient.client.platform.mc.screen;

import io.github.solclient.client.platform.mc.DrawableHelper;
import io.github.solclient.client.platform.mc.MinecraftClient;
import io.github.solclient.client.platform.mc.text.Font;
import io.github.solclient.client.platform.mc.text.Text;

public class ExtensibleScreen implements Screen {

	protected final MinecraftClient mc;
	protected final Font font;

	public ExtensibleScreen(Text title) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void render(int mouseX, int mouseY, float tickDelta) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void keyDown(char character, int key) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void keyUp(char character, int key) {
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

	@Override
	public void scroll(int by) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void update(MinecraftClient mc, int width, int height) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void init() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void close() {
		throw new UnsupportedOperationException();
	}

	protected void renderBackground() {
		throw new UnsupportedOperationException();
	}

	protected void renderTranslucentBackground() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getWidth() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getHeight() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void tick() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean shouldPauseGame() {
		return true;
	}

}
