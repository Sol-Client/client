package io.github.solclient.abstraction.mc.text;

import org.jetbrains.annotations.NotNull;

import io.github.solclient.abstraction.VirtualEnum;

public interface ClickEvent {

	static ClickEvent create(Action action, String value) {
		throw new UnsupportedOperationException();
	}

	@NotNull Action getAction();

	@NotNull String getValue();

	interface Action extends VirtualEnum {

		Action OPEN_URL = null;
		Action OPEN_FILE = null;
		Action RUN_COMMAND = null;
		Action SUGGEST_COMMAND = null;
		Action CHANGE_PAGE = null;
		Action COPY_TO_CLIPBOARD = null;

	}

}
