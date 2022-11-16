package io.github.solclient.client.ui.component.handler;

import io.github.solclient.client.ui.component.ComponentRenderInfo;

@FunctionalInterface
public interface KeyHandler {

	boolean keyPressed(ComponentRenderInfo info, int code, int scancode, int mods);

}
