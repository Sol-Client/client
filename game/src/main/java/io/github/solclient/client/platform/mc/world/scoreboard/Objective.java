package io.github.solclient.client.platform.mc.world.scoreboard;

import org.jetbrains.annotations.NotNull;

import io.github.solclient.client.platform.mc.text.Text;

public interface Objective {

	@NotNull Scoreboard getScoreboard();

	@NotNull Text getDisplayName();

}
