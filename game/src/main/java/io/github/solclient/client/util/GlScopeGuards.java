package io.github.solclient.client.util;

import io.github.solclient.client.platform.mc.render.GlStateManager;
import lombok.experimental.UtilityClass;

@UtilityClass
public class GlScopeGuards {

	private final ScopeGuard POP = GlStateManager::popMatrix;

	public ScopeGuard push() {
		GlStateManager.pushMatrix();
		return POP;
	}

}
