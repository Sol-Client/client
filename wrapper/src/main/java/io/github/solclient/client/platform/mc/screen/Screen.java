package io.github.solclient.client.platform.mc.screen;

import io.github.solclient.client.platform.mc.MinecraftClient;

public interface Screen {

	void update(MinecraftClient mc, int width, int height);

	void initScreen();

	void renderScreen(int mouseX, int mouseY, float tickDelta);

	boolean characterTyped(char character, int key);

	boolean keyDown(int code, int scancode, int mods);

	boolean mouseDown(int x, int y, int button);

	boolean mouseUp(int x, int y, int button);

	int getWidth();

	int getHeight();

	void tickScreen();

}
