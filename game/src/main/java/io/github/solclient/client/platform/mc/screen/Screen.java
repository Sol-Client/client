package io.github.solclient.client.platform.mc.screen;

import io.github.solclient.client.platform.mc.DrawableHelper;

public interface Screen extends DrawableHelper {

	void init();

	void close();

	void render(int mouseX, int mouseY, float tickDelta);

	void keyDown(char character, int key);

	void keyUp(char character, int key);

	void mouseDown(int x, int y, int button);

	void mouseUp(int x, int y, int button);

	void scroll(int by);

}
