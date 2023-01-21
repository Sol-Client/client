package io.github.solclient.client.mod.impl.replay.fix;

import io.github.solclient.client.util.ForgeCompat;
import net.minecraft.client.render.WorldRenderer;

@Deprecated
@ForgeCompat
public class SCForgeHooksClient {

	public static boolean renderFirstPersonHand(WorldRenderer context, float partialTicks, int renderPass) {
		return false; // Always render hand
	}

}
