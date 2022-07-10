package io.github.solclient.client.platform.mc.world.scoreboard;

import org.jetbrains.annotations.NotNull;

import io.github.solclient.client.platform.Helper;
import io.github.solclient.client.platform.mc.text.Text;

public interface PlayerTeam {

	@Helper
	@NotNull Text formatText(@NotNull String text, boolean numbers);

}
