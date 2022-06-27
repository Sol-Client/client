package io.github.solclient.abstraction.mc.world.scoreboard;

import org.jetbrains.annotations.NotNull;

public interface Score {

	@NotNull String getOwner();

	int getValue();

	@NotNull String getPlayerName();

}
