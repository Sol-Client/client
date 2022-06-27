package io.github.solclient.client.command;

import java.util.List;

import org.jetbrains.annotations.NotNull;

import io.github.solclient.abstraction.mc.world.entity.player.LocalPlayer;

// TODO replace with brigadier, and incorporate a client-sided brigadier in 1.8.
// TODO command translations.
public interface Command {

	void execute(@NotNull LocalPlayer player, @NotNull List<String> args) throws CommandException;

	default String[] getAliases() {
		return new String[0];
	}

}
