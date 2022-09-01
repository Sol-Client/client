package io.github.solclient.client.mod.impl.hud.ping;

import com.google.gson.annotations.Expose;

import io.github.solclient.client.event.EventHandler;
import io.github.solclient.client.event.impl.game.PostTickEvent;
import io.github.solclient.client.event.impl.network.ServerConnectEvent;
import io.github.solclient.client.mod.annotation.Option;
import io.github.solclient.client.mod.hud.SmoothCounterHudMod;
import io.github.solclient.client.platform.mc.network.PlayerListEntry;

public class PingMod extends SmoothCounterHudMod {

	public static final PingMod INSTANCE = new PingMod();

	private static final int PING_INTERVAL = 600;

	@Expose
	@Option
	private PingSource source = PingSource.AUTO;

	private int ping, nextPing;

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
		if(source.resolve() != PingSource.MULTIPLAYER_SCREEN) {
			return;
		}

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
		if(ping != -1 && source.resolve() == PingSource.MULTIPLAYER_SCREEN) {
			return ping;
		}

		if(source.resolve() == PingSource.TAB_LIST && !mc.hasSingleplayerServer() && mc.hasPlayer()
				&& mc.getCurrentServer() != null) {
			PlayerListEntry entry = mc.getPlayer().getConnection().getPlayerListEntry(mc.getPlayer().getId());
			if(entry != null) {
				return entry.getPing();
			}
		}

		return 0;
	}

	@Override
	public String getSuffix() {
		return "ms";
	}

}
