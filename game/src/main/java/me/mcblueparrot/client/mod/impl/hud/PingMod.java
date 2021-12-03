package me.mcblueparrot.client.mod.impl.hud;

import me.mcblueparrot.client.mod.hud.SmoothCounterHudMod;
import net.minecraft.client.network.NetworkPlayerInfo;

public class PingMod extends SmoothCounterHudMod {

	public PingMod() {
		super("Ping", "ping", "Display the latency to the server.");
	}

	@Override
	public int getIntValue() {
		if(mc.getCurrentServerData() != null && !mc.isIntegratedServerRunning()) {
			NetworkPlayerInfo info = mc.thePlayer.sendQueue.getPlayerInfo(mc.thePlayer.getGameProfile().getId());
			if(info != null) {
				return info.getResponseTime();
			}
		}

		return 0;
	}

	@Override
	public String getSuffix() {
		return "ms";
	}

}
