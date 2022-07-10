package io.github.solclient.client.platform.mc.world.level;

import io.github.solclient.client.platform.mc.maths.Box;

public interface LevelRenderer {

	static void strokeBox(Box offsetBox, int red, int green, int blue, int alpha) {
		throw new UnsupportedOperationException();
	}

	void scheduleUpdate();

}
