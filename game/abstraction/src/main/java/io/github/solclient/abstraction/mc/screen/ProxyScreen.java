package io.github.solclient.abstraction.mc.screen;

import io.github.solclient.abstraction.mc.DrawableHelper;
import io.github.solclient.abstraction.mc.MinecraftClient;
import io.github.solclient.abstraction.mc.text.Font;
import io.github.solclient.abstraction.mc.text.Text;

/**
 * Used to extend an interface.
 * My poor head.
 */
public class ProxyScreen implements Screen, DrawableHelper {

	protected final MinecraftClient mc;
	protected final Font font;

	public ProxyScreen(Text title) {
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

	protected int getWidth() {
		throw new UnsupportedOperationException();
	}

	protected int getHeight() {
		throw new UnsupportedOperationException();
	}

}
