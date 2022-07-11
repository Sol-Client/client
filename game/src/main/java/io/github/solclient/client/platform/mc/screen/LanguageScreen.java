package io.github.solclient.client.platform.mc.screen;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import io.github.solclient.client.platform.mc.lang.LanguageManager;
import io.github.solclient.client.platform.mc.option.Options;

public interface LanguageScreen extends Screen {

	static @NotNull LanguageScreen create(@Nullable Screen parent, @NotNull Options options, @NotNull LanguageManager languages) {
		throw new UnsupportedOperationException();
	}

}
