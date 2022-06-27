package io.github.solclient.abstraction.mc.network;

import org.jetbrains.annotations.NotNull;

import io.github.solclient.abstraction.mc.world.entity.player.GameMode;

public interface LocalPlayerState {

	@NotNull GameMode getGameMode();

}
