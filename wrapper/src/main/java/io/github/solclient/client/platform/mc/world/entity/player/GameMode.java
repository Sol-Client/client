package io.github.solclient.client.platform.mc.world.entity.player;

import org.jetbrains.annotations.NotNull;

import io.github.solclient.client.platform.VirtualEnum;

public interface GameMode extends VirtualEnum {

	GameMode SURVIVAL = null;
	GameMode CREATIVE = null;
	GameMode ADVENTURE = null;
	GameMode SPECTATOR = null;

	@NotNull String getId();

	int getNumericId();

}
