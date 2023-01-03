package io.github.solclient.client.mod.impl.hud.ping;

import io.github.solclient.client.*;
import net.minecraft.client.resources.I18n;

public enum PingSource {
	AUTO, MULTIPLAYER_SCREEN, TAB_LIST;

	public PingSource resolve() {
		if (this == AUTO) {
			if (Client.INSTANCE.detectedServer == DetectedServer.HYPIXEL) {
				return MULTIPLAYER_SCREEN;
			}

			return TAB_LIST;
		}

		return this;
	}

	@Override
	public String toString() {
		return I18n.format("sol_client.mod.ping.option.source." + name().toLowerCase());
	}

}
