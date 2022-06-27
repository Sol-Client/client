package io.github.solclient.abstraction.mc.screen;

import io.github.solclient.abstraction.mc.DrawableHelper;

public interface Screen extends DrawableHelper {

	void render(int mouseX, int mouseY, float tickDelta);

	void type(char character, int key);

}
