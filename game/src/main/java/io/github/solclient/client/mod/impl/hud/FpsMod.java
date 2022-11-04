package io.github.solclient.client.mod.impl.hud;

import io.github.solclient.client.mod.hud.SmoothCounterHudMod;
import io.github.solclient.client.platform.mc.MinecraftClient;

public final class FpsMod extends SmoothCounterHudMod {

	public static final FpsMod INSTANCE = new FpsMod();

	@Override
	public String getId() {
		return "fps";
	}

	@Override
	public int getIntValue() {
		return MinecraftClient.getFps();
	}

	@Override
	public String getSuffix() {
		return "FPS";
	}

}
