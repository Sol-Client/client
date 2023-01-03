package io.github.solclient.client.mod.impl.hypixeladditions.commands;

import io.github.solclient.client.mod.impl.hypixeladditions.HypixelAdditionsMod;
import lombok.RequiredArgsConstructor;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;

@RequiredArgsConstructor
public abstract class HypixelAdditionsCommand extends CommandBase {

	protected final HypixelAdditionsMod mod;
	protected final Minecraft mc = Minecraft.getMinecraft();

}
