package io.github.solclient.client.mod.impl.hypixeladditions.commands;

import io.github.solclient.client.command.Command;
import io.github.solclient.client.mod.impl.hypixeladditions.HypixelAdditionsMod;
import io.github.solclient.client.platform.mc.MinecraftClient;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class HypixelAdditionsCommand implements Command {

	protected final HypixelAdditionsMod mod;
	protected final MinecraftClient mc = MinecraftClient.getInstance();

}
