package io.github.solclient.client.mod.impl.hypixeladditions.commands;

import io.github.solclient.client.mod.impl.hypixeladditions.HypixelAdditionsMod;
import lombok.RequiredArgsConstructor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.AbstractCommand;

@RequiredArgsConstructor
public abstract class HypixelAdditionsCommand extends AbstractCommand {

	protected final HypixelAdditionsMod mod;
	protected final MinecraftClient mc = MinecraftClient.getInstance();

}
