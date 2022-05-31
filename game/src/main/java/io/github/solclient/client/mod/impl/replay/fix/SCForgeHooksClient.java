package io.github.solclient.client.mod.impl.replay.fix;

import io.github.solclient.client.annotation.ForgeCompat;
import net.minecraft.client.renderer.RenderGlobal;

@Deprecated
@ForgeCompat
public class SCForgeHooksClient {

	public static boolean renderFirstPersonHand(RenderGlobal context, float partialTicks, int renderPass) {
		return false; // Always render hand
	}

}
