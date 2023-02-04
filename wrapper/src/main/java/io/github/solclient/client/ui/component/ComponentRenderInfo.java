package io.github.solclient.client.ui.component;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(fluent = true)
public class ComponentRenderInfo {

	private final float relativeMouseX, relativeMouseY;
	private final float tickDelta;

}
