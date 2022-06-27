package io.github.solclient.client.mod.impl.hud;

import io.github.solclient.client.event.EventHandler;
import io.github.solclient.client.event.impl.game.PostTickEvent;
import io.github.solclient.client.event.impl.network.ServerConnectEvent;
import io.github.solclient.client.mod.hud.SmoothCounterHudMod;

public class PingMod extends SmoothCounterHudMod {

	private int ping;
	private static final int PING_INTERVAL = 600;
	private int nextPing;

	@Override
	public String getId() {
		return "ping";
	}

	@EventHandler
	public void onServerConnect(ServerConnectEvent event) {
		ping = 0;
		nextPing = 60;
	}

	@EventHandler
	public void updatePing(PostTickEvent event) {
		if(mc.getCurrentServer() != null && !mc.hasSingleplayerServer()) {
			if(nextPing > 0) {
				nextPing--;
			}
			else if(nextPing > -1) {
				nextPing = -1;


				mc.getCurrentServer().ping().thenAccept((newPing) -> {
					if(newPing != -1) {
						if(ping != 0) {
							ping = (ping * 3 + newPing) / 4;
						}
						else {
							ping = newPing;
						}
					}

					nextPing = PING_INTERVAL;
				});
			}
		}
	}

	@Override
	public int getIntValue() {
		if(ping != -1) {
			return ping;
		}

		return 0;
	}

	@Override
	public String getSuffix() {
		return "ms";
	}

}
