package io.github.solclient.abstraction.mc.world.scoreboard;

import java.util.Collection;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Scoreboard {

	@NotNull Collection<Score> getScores(Objective objective);

	@Nullable PlayerTeam getPlayersTeam(@NotNull String playerName);

}
