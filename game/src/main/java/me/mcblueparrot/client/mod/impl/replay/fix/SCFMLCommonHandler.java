package me.mcblueparrot.client.mod.impl.replay.fix;

import me.mcblueparrot.client.annotation.ForgeCompat;

@Deprecated
@ForgeCompat
public class SCFMLCommonHandler {

	private static final SCFMLCommonHandler INSTANCE = new SCFMLCommonHandler();

	public static SCFMLCommonHandler instance() {
		return INSTANCE;
	}

	public void onRenderTickStart(float partialTicks) {
		// Stub
	}

	public void onRenderTickEnd(float partialTicks) {
		// Stub
	}

}
