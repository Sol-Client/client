package io.github.solclient.client.platform.mc.world.scoreboard;

import org.jetbrains.annotations.NotNull;

public interface Score {

	@NotNull String getOwner();

	int getValue();

	@NotNull String getPlayerName();

}
