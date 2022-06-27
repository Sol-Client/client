package io.github.solclient.abstraction.mc.world.scoreboard;

import org.jetbrains.annotations.NotNull;

import io.github.solclient.abstraction.mc.text.Text;

public interface Objective {

	@NotNull Scoreboard getScoreboard();

	@NotNull Text getDisplayName();

}
