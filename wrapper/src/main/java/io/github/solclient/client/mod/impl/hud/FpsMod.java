package io.github.solclient.client.mod.impl.hud;

import io.github.solclient.client.mod.hud.SmoothCounterHudMod;
import net.minecraft.client.MinecraftClient;

public class FpsMod extends SmoothCounterHudMod {

	@Override
	public String getId() {
		return "fps";
	}

	@Override
	public int getIntValue() {
		return MinecraftClient.getCurrentFps();
	}

	@Override
	public String getSuffix() {
		return "FPS";
	}

}
