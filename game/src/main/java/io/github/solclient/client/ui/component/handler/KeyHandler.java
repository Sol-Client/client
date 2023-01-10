package io.github.solclient.client.ui.component.handler;

import io.github.solclient.client.ui.component.ComponentRenderInfo;

@FunctionalInterface
public interface KeyHandler {

	boolean onKey(ComponentRenderInfo info, int keyCode, char character);

}
