package me.mcblueparrot.client.mod.impl.hud;

import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import me.mcblueparrot.client.event.EventHandler;
import me.mcblueparrot.client.event.impl.PostTickEvent;
import me.mcblueparrot.client.event.impl.ServerConnectEvent;
import me.mcblueparrot.client.mod.hud.SmoothCounterHudMod;
import me.mcblueparrot.client.util.Utils;

public class PingMod extends SmoothCounterHudMod {

	private int ping;
	private static final int PING_INTERVAL = 600;
	private int nextPing;

	public PingMod() {
		super("Ping", "ping", "Display the latency to the server.");
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
