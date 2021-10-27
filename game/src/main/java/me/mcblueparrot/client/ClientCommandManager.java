package me.mcblueparrot.client;

import me.mcblueparrot.client.mod.impl.HypixelAdditionsMod;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;

public class ClientCommandManager {

    public static CommandBase get(String name) {
        Minecraft mc = Minecraft.getMinecraft();
        if(HypixelAdditionsMod.isEffective() && HypixelAdditionsMod.instance.visitHousingCommand) {
            if(name.equals("visithousing")) {
                return HypixelAdditionsMod.instance.new VisitHousingCommand();
            }
        }
        return null;
    }

}
