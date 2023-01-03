package io.github.solclient.client.mod.impl.hud.ping;

import java.net.UnknownHostException;

import com.google.gson.annotations.Expose;

import io.github.solclient.client.event.EventHandler;
import io.github.solclient.client.event.impl.*;
import io.github.solclient.client.mod.annotation.Option;
import io.github.solclient.client.mod.hud.SmoothCounterHudMod;
import io.github.solclient.client.util.Utils;
import net.minecraft.client.network.NetworkPlayerInfo;

public class PingMod extends SmoothCounterHudMod {

	private static final int PING_INTERVAL = 600;

	private int ping;
	private int nextPing;

	@Expose
	@Option
	private PingSource source = PingSource.AUTO;

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
		if (source.resolve() != PingSource.MULTIPLAYER_SCREEN) {
			return;
		}

		if (mc.getCurrentServerData() != null && !mc.isIntegratedServerRunning()) {
			if (nextPing > 0) {
				nextPing--;
			} else if (nextPing > -1) {
				nextPing = -1;

				Thread thread = new Thread(() -> {
					try {
						Utils.pingServer(mc.getCurrentServerData().serverIP, (newPing) -> {
							if (newPing != -1) {
								if (ping != 0) {
									ping = (ping * 3 + newPing) / 4;
								} else {
									ping = newPing;
								}
							}
						});
					} catch (UnknownHostException error) {
						logger.error("Could not ping server", error);
					}

					nextPing = PING_INTERVAL;
				});
				thread.setDaemon(true);
				thread.start();
			}
		}
	}

	@Override
	public int getIntValue() {
		if (ping != -1 && source.resolve() == PingSource.MULTIPLAYER_SCREEN) {
			return ping;
		}

		if (source.resolve() == PingSource.TAB_LIST && !mc.isIntegratedServerRunning() && mc.thePlayer != null
				&& mc.getCurrentServerData() != null) {
			NetworkPlayerInfo info = mc.thePlayer.sendQueue.getPlayerInfo(mc.getSession().getProfile().getId());
			if (info != null) {
				return info.getResponseTime();
			}
		}

		return 0;
	}

	@Override
	public String getSuffix() {
		return "ms";
	}

}
