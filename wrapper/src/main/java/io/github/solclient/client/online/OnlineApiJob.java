package io.github.solclient.client.online;

import java.io.IOException;
import java.time.*;

import io.github.solclient.client.Client;
import io.github.solclient.client.event.EventHandler;
import io.github.solclient.client.event.impl.*;
import io.github.solclient.client.util.MinecraftUtils;

public final class OnlineApiJob {

	private ZonedDateTime lastUpdate;

	@EventHandler
	public void onWorldLoad(WorldLoadEvent event) {
		OnlineApi.clearCache();
	}

	@EventHandler
	public void onTick(PreTickEvent event) throws IOException {
		if (Duration.between(lastUpdate, ZonedDateTime.now()).toMinutes() >= 29) {
			lastUpdate = ZonedDateTime.now();
			OnlineApi.logIn(MinecraftUtils.getPlayerUuid());
		}
	}

	@EventHandler
	public void onPostStart(PostGameStartEvent event) {
		try {
			OnlineApi.logIn(MinecraftUtils.getPlayerUuid());
			lastUpdate = ZonedDateTime.now();
		} catch (IOException error) {
			Client.INSTANCE.getEvents().unregister(this);
		}
	}

	@EventHandler
	public void onQuit(GameQuitEvent event) throws IOException {
		OnlineApi.logOut(MinecraftUtils.getPlayerUuid());
	}

}
