package io.github.solclient.client.mod.impl;

import java.text.DecimalFormat;

import io.github.solclient.client.*;
import io.github.solclient.client.mod.*;
import io.github.solclient.client.platform.mc.MinecraftClient;
import io.github.solclient.client.platform.mc.text.*;
import io.github.solclient.client.platform.mc.world.entity.PrimedTnt;

public final class TNTTimerMod extends Mod {

	public static final TNTTimerMod INSTANCE = new TNTTimerMod();
	private static final DecimalFormat FORMAT = new DecimalFormat("0.00");

	@Override
	public String getId() {
		return "tnt_timer";
	}

	@Override
	public ModCategory getCategory() {
		return ModCategory.UTILITY;
	}

	// Unfortunately doesn't work with TNT chains due to their random nature.
	public static Text getText(PrimedTnt tnt) {
		float fuse = tnt.getFuse();

		// Based on Sk1er's mod
		if(Client.INSTANCE.detectedServer == DetectedServer.HYPIXEL
				&& Text.plainEquals(MinecraftClient.getInstance().getLevel().getScoreboardTitle(), "BED WARS")) {
			fuse -= 28;
		}

		TextColour colour = TextColour.GREEN;

		if(fuse < 20) {
			colour = TextColour.DARK_RED;
		}
		else if(fuse < 40) {
			colour = TextColour.RED;
		}
		else if(fuse < 60) {
			colour = TextColour.GOLD;
		}

		final TextColour finalColour = colour;
		return Text.literal(FORMAT.format(fuse / 20)).style((style) -> style.withColour(finalColour));
	}

}
