package io.github.solclient.client.command;

import java.util.List;

import java.util.List;

import org.jetbrains.annotations.NotNull;

import io.github.solclient.api.world.entity.LocalPlayer;
import io.github.solclient.api.world.entity.Player;

public interface Command {

	void execute(@NotNull LocalPlayer player, @NotNull List<String> args);

	default String[] getCommandAliases() {
		return new String[0];
	}

}
