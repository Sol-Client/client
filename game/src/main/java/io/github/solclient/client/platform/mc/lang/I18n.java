package io.github.solclient.client.platform.mc.lang;

import java.util.Optional;

import org.jetbrains.annotations.NotNull;

import lombok.experimental.UtilityClass;

@UtilityClass
public class I18n {

	public @NotNull String translate(@NotNull String key) {
		throw new UnsupportedOperationException();
	}

	public @NotNull String translate(@NotNull String key, @NotNull Object... values) {
		throw new UnsupportedOperationException();
	}

	public @NotNull Optional<String> translateOpt(@NotNull String key) {
		throw new UnsupportedOperationException();
	}

	public @NotNull Optional<String> translateOpt(@NotNull String key, @NotNull Object... values) {
		throw new UnsupportedOperationException();
	}

}
