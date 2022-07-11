package io.github.solclient.client.platform.mc.text;

import org.jetbrains.annotations.NotNull;

public interface I18nText extends Text {

	static @NotNull I18nText create(String key, Object... args) {
		throw new UnsupportedOperationException();
	}

}
