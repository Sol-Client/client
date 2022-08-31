package io.github.solclient.client.platform.mc.text;

import io.github.solclient.client.platform.mc.util.OperatingSystem;
import org.jetbrains.annotations.NotNull;

import io.github.solclient.client.platform.VirtualEnum;

public interface ClickEvent {

	static ClickEvent create(Action action, String value) {
		throw new UnsupportedOperationException();
	}

	@NotNull Action getAction();

	@NotNull String getValue();

	interface Action extends VirtualEnum {

		Action OPEN_URL = get("OPEN_URL"),
				OPEN_FILE = get("OPEN_FILE"),
				RUN_COMMAND = get("RUN_COMMAND"),
				SUGGEST_COMMAND = get("SUGGEST_COMMAND"),
				CHANGE_PAGE = get("CHANGE_PAGE");

		static Action get(String name) {
			throw new UnsupportedOperationException();
		}

	}

}
