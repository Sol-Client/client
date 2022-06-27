package io.github.solclient.client.command;

import java.util.List;

import java.util.List;

import org.jetbrains.annotations.NotNull;

import io.github.solclient.abstraction.mc.world.entity.player.LocalPlayer;
import io.github.solclient.abstraction.mc.world.entity.player.Player;

public interface Command {

	void execute(@NotNull LocalPlayer player, @NotNull List<String> args) throws CommandException;

	default String[] getCommandAliases() {
		return new String[0];
	}

}
