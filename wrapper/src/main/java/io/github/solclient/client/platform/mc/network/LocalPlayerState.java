package io.github.solclient.client.platform.mc.network;

import org.jetbrains.annotations.NotNull;

import io.github.solclient.client.platform.mc.world.entity.player.GameMode;

public interface LocalPlayerState {

	@NotNull GameMode getGameMode();

}
