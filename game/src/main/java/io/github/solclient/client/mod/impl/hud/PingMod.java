package io.github.solclient.client.mod.impl.hud;

import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.github.solclient.client.event.EventHandler;
import io.github.solclient.client.event.impl.PostTickEvent;
import io.github.solclient.client.event.impl.ServerConnectEvent;
import io.github.solclient.client.mod.hud.SmoothCounterHudMod;
import io.github.solclient.client.util.Utils;

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
		if(mc.getCurrentServerData() != null && !mc.isIntegratedServerRunning()) {
			if(nextPing > 0) {
				nextPing--;
			}
			else if(nextPing > -1) {
				nextPing = -1;

				Utils.MAIN_EXECUTOR.submit(() -> {
					try {
						Utils.pingServer(mc.getCurrentServerData().serverIP, (newPing) -> {
							if(newPing != -1) {
								if(ping != 0) {
									ping = (ping * 3 + newPing) / 4;
								}
								else {
									ping = newPing;
								}
							}
						});
					}
					catch(UnknownHostException error) {
						error.printStackTrace();
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
