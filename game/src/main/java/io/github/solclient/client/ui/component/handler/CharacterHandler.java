package io.github.solclient.client.ui.component.handler;

import io.github.solclient.client.ui.component.ComponentRenderInfo;

@FunctionalInterface
public interface CharacterHandler {

	boolean characterTyped(ComponentRenderInfo info, char character);

}
