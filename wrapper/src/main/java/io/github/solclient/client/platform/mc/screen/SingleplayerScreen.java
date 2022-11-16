package io.github.solclient.client.platform.mc.screen;

import org.jetbrains.annotations.*;

public interface SingleplayerScreen extends Screen {

	static @NotNull SingleplayerScreen create(@Nullable Screen parent) {
		throw new UnsupportedOperationException();
	}

}
