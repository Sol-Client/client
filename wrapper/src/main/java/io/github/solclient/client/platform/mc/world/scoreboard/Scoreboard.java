package io.github.solclient.client.platform.mc.world.scoreboard;

import java.util.Collection;

import org.jetbrains.annotations.*;

public interface Scoreboard {

	@NotNull Collection<Score> getScores(Objective objective);

	@Nullable PlayerTeam getPlayersTeam(@NotNull String playerName);

}
