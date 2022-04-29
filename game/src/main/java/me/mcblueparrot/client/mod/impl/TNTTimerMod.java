package me.mcblueparrot.client.mod.impl;

import java.text.DecimalFormat;

import me.mcblueparrot.client.Client;
import me.mcblueparrot.client.DetectedServer;
import me.mcblueparrot.client.mod.Mod;
import me.mcblueparrot.client.mod.ModCategory;
import me.mcblueparrot.client.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.util.EnumChatFormatting;

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
	public static String getText(EntityTNTPrimed tnt) {
		float fuse = tnt.fuse;

		// Based on Sk1er's mod
		if(Client.INSTANCE.detectedServer == DetectedServer.HYPIXEL && "BED WARS".equals(Utils.getScoreboardTitle())) {
			fuse -= 28;
		}

		EnumChatFormatting colour = EnumChatFormatting.GREEN;

		if(fuse < 20) {
			colour = EnumChatFormatting.DARK_RED;
		}
		else if(fuse < 40) {
			colour = EnumChatFormatting.RED;
		}
		else if (fuse < 60) {
			colour = EnumChatFormatting.GOLD;
		}

		return colour + FORMAT.format(fuse / 20);
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
