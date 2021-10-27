package me.mcblueparrot.client.mod.impl.hud;

import me.mcblueparrot.client.mod.hud.SimpleHud;
import net.minecraft.client.network.NetworkPlayerInfo;

public class PingHud extends SimpleHud {

    public PingHud() {
        super("Ping", "ping", "Display the latency to the server.");
    }

    @Override
    public String getText(boolean editMode) {
        if(!(editMode || mc.getCurrentServerData() == null)) {
            NetworkPlayerInfo info = mc.thePlayer.sendQueue.getPlayerInfo(mc.thePlayer.getGameProfile().getId());
            if(info != null) {
                return info.getResponseTime() + " ms";
            }
        }
        return "0 ms";
    }

}
