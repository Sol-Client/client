package io.github.solclient.client.platform.mc.text;

import org.jetbrains.annotations.NotNull;

import io.github.solclient.client.platform.VirtualEnum;

public interface ClickEvent {

	static ClickEvent create(Action action, String value) {
		throw new UnsupportedOperationException();
	}

	@NotNull Action getAction();

	@NotNull String getValue();

	interface Action extends VirtualEnum {

		Action OPEN_URL = null,
				OPEN_FILE = null,
				RUN_COMMAND = null,
				SUGGEST_COMMAND = null,
				CHANGE_PAGE = null,
				COPY_TO_CLIPBOARD = null;

	}

}
