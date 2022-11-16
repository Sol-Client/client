package io.github.solclient.client.ui.component;

import lombok.Data;

@Data
public final class ComponentRenderInfo {

	private final int relativeMouseX, relativeMouseY;
	private final float partialTicks;

}
