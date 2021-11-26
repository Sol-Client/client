package me.mcblueparrot.client.mod.impl.hud;

import java.text.DecimalFormat;

import me.mcblueparrot.client.mod.hud.SimpleHud;
import net.minecraft.util.MathHelper;

public class SpeedHud extends SimpleHud {

	private static final DecimalFormat FORMAT = new DecimalFormat("0.00");

	public SpeedHud() {
		super("Speedometer", "speed", "Display your speed on the HUD.");
	}

	@Override
	public String getText(boolean editMode) {
		if(editMode) {
			return "0.00 m/s";
		}
		else {
			double distTraveledLastTickX = mc.thePlayer.posX - mc.thePlayer.prevPosX;
			double distTraveledLastTickZ = mc.thePlayer.posZ - mc.thePlayer.prevPosZ;
			double currentSpeed = MathHelper.sqrt_double(distTraveledLastTickX * distTraveledLastTickX
					+ distTraveledLastTickZ * distTraveledLastTickZ);
			return FORMAT.format(currentSpeed / 0.05F) + " m/s";
		}
	}

}
