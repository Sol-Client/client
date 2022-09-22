package io.github.solclient.client.platform.mc.screen;

import io.github.solclient.client.platform.mc.MinecraftClient;
import io.github.solclient.client.platform.mc.text.Font;
import io.github.solclient.client.platform.mc.text.Text;

public class ProxyScreen implements Screen {

	protected int width, height;
	protected MinecraftClient mc;
	protected Font font;

	public ProxyScreen(Text title) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void renderScreen(int mouseX, int mouseY, float tickDelta) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean characterTyped(char character, int key) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean keyDown(int code, int scancode, int mods) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean mouseDown(int x, int y, int button) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean mouseUp(int x, int y, int button) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Called when the mouse is scrolled.
	 * You should not call super.
	 * @param by Scroll amount.
	 */
	protected void scroll(int by) {
	}

	@Override
	public final void update(MinecraftClient mc, int width, int height) {}

	@Override
	public void initScreen() {
		throw new UnsupportedOperationException();
	}

	protected void onClose() {
	}

	protected final void renderDefaultBackground() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void tickScreen() {
		throw new UnsupportedOperationException();
	}

	protected boolean pausesGame() {
		return true;
	}

	@Override
	public final int getWidth() {
		return width;
	}

	@Override
	public final int getHeight() {
		return height;
	}

}
