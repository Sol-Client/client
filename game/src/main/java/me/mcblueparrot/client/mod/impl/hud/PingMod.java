package me.mcblueparrot.client.mod.impl.hud;

import me.mcblueparrot.client.mod.hud.SimpleHudMod;
import net.minecraft.client.network.NetworkPlayerInfo;

public class PingMod extends SimpleHudMod {

	public PingMod() {
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
