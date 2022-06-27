package io.github.solclient.abstraction.mc.world.entity.player;

import org.jetbrains.annotations.NotNull;

import io.github.solclient.abstraction.VirtualEnum;

public interface GameMode extends VirtualEnum {

	GameMode SURVIVAL = null;
	GameMode CREATIVE = null;
	GameMode ADVENTURE = null;
	GameMode SPECTATOR = null;

	@NotNull String getId();

	int getNumericId();

}
