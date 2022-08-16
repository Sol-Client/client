package io.github.solclient.client.platform.mc.screen;

import io.github.solclient.client.platform.mc.DrawableHelper;
import io.github.solclient.client.platform.mc.MinecraftClient;

public interface Screen {

	void update(MinecraftClient mc, int width, int height);

	void initScreen();

	void onClose();

	void renderScreen(int mouseX, int mouseY, float tickDelta);

	void keyDown(char character, int key);

	void mouseDown(int x, int y, int button);

	void mouseUp(int x, int y, int button);

	int getWidth();

	int getHeight();

	void tickScreen();

}
