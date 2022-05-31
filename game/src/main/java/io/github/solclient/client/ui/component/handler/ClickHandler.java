package io.github.solclient.client.ui.component.handler;

import io.github.solclient.client.ui.component.ComponentRenderInfo;

@FunctionalInterface
public interface ClickHandler {

	boolean onClick(ComponentRenderInfo info, int button);

}
