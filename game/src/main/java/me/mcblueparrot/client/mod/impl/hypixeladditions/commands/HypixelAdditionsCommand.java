package me.mcblueparrot.client.mod.impl.hypixeladditions.commands;

import lombok.RequiredArgsConstructor;
import me.mcblueparrot.client.mod.impl.hypixeladditions.HypixelAdditionsMod;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;

@RequiredArgsConstructor
public abstract class HypixelAdditionsCommand extends CommandBase {

    protected final HypixelAdditionsMod mod;
    protected Minecraft mc = Minecraft.getMinecraft();
    
}
