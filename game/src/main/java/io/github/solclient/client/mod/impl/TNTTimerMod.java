package io.github.solclient.client.mod.impl;

import java.text.DecimalFormat;

import io.github.solclient.abstraction.mc.MinecraftClient;
import io.github.solclient.abstraction.mc.text.LiteralText;
import io.github.solclient.abstraction.mc.text.Text;
import io.github.solclient.abstraction.mc.text.TextColour;
import io.github.solclient.abstraction.mc.text.TextFormatting;
import io.github.solclient.abstraction.mc.world.entity.PrimedTnt;
import io.github.solclient.client.Client;
import io.github.solclient.client.DetectedServer;
import io.github.solclient.client.mod.Mod;
import io.github.solclient.client.mod.ModCategory;
import io.github.solclient.client.util.Utils;

public class TNTTimerMod extends Mod {

	private static final DecimalFormat FORMAT = new DecimalFormat("0.00");
	public static boolean enabled;

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
				&& "BED WARS".equals(MinecraftClient.getInstance().getLevel().getScoreboardTitle())) {
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
		return LiteralText.create(FORMAT.format(fuse / 20)).withStyle((style) -> style.setColour(finalColour));
	}

	@Override
	protected void onEnable() {
		super.onEnable();
		enabled = true;
	}

	@Override
	protected void onDisable() {
		super.onDisable();
		enabled = false;
	}

}
