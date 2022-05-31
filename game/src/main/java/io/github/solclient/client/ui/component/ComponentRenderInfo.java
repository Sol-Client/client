package io.github.solclient.client.ui.component;

import lombok.Data;

@Data
public class ComponentRenderInfo {

	private final int relativeMouseX;
	private final int relativeMouseY;
	private final float partialTicks;

}
