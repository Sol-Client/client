package io.github.solclient.abstraction.mc.world.scoreboard;

import org.jetbrains.annotations.NotNull;

import io.github.solclient.abstraction.Helper;
import io.github.solclient.abstraction.mc.text.Text;

public interface PlayerTeam {

	@Helper
	@NotNull Text formatText(@NotNull String text, boolean numbers);

}
