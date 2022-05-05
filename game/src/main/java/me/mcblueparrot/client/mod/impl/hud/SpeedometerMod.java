package me.mcblueparrot.client.mod.impl.hud;

import java.text.DecimalFormat;
import java.util.Arrays;

import org.lwjgl.opengl.GL11;

import com.google.gson.annotations.Expose;

import me.mcblueparrot.client.event.EventHandler;
import me.mcblueparrot.client.event.impl.GameOverlayElement;
import me.mcblueparrot.client.event.impl.PostGameOverlayRenderEvent;
import me.mcblueparrot.client.event.impl.PreTickEvent;
import me.mcblueparrot.client.mod.annotation.Option;
import me.mcblueparrot.client.mod.hud.SimpleHudMod;
import me.mcblueparrot.client.ui.screen.mods.MoveHudsScreen;
import me.mcblueparrot.client.util.Utils;
import me.mcblueparrot.client.util.access.AccessMinecraft;
import me.mcblueparrot.client.util.data.Colour;
import me.mcblueparrot.client.util.data.Position;
import me.mcblueparrot.client.util.data.Rectangle;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.MathHelper;

public class SpeedometerMod extends SimpleHudMod {

	private static final int SPEED_COUNT = 200;

	@Expose
	@Option
	private boolean graphMode;

	private static final DecimalFormat FORMAT = new DecimalFormat("0.00");
	private double[] speeds = new double[SPEED_COUNT];
	private long lastUpdate;

	@Override
	public String getId() {
		return "speedometer";
	}

	public void addSpeed(double speed) {
		System.arraycopy(speeds, 1, speeds, 0, SPEED_COUNT - 1);
		speeds[SPEED_COUNT - 1] = speed;
	}

	@Override
	public Rectangle getBounds(Position position) {
		if(!graphMode) {
			return super.getBounds(position);
		}

		return super.getBounds(position).multiply(1.5F, 1.5F);
	}

	@Override
	public void render(Position position, boolean editMode) {
		super.render(position, editMode);

		if(graphMode) {
			double[] speeds = this.speeds;

			if(editMode) {
				speeds = new double[SPEED_COUNT];
			}

			float[] bounds = element.getHighPrecisionMultipliedBounds();
			Utils.scissor(bounds[0], bounds[1], bounds[2], bounds[3]);
			Utils.glColour(textColour);
			GL11.glLineWidth(1.5F);

			if(!editMode && !mc.isGamePaused()
					&& (lastUpdate == -1 || (System.currentTimeMillis() - lastUpdate) > 30)) {
				addSpeed(getSpeed());
				lastUpdate = System.currentTimeMillis();
			}

			GL11.glEnable(GL11.GL_BLEND);
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GL11.glEnable(GL11.GL_LINE_SMOOTH);
			GL11.glShadeModel(GL11.GL_SMOOTH);
			GL11.glEnable(GL11.GL_SCISSOR_TEST);

			GL11.glBegin(GL11.GL_LINE_STRIP);

			for(int i = 0; i < SPEED_COUNT; i++) {
				GL11.glVertex2d(position.getX() + (i * (((getBounds(position).getWidth() + 0.4) / (double) SPEED_COUNT))),
						position.getY() - 2 + getBounds(position).getHeight() - (speeds[i] * 16));
			}

			GL11.glEnd();
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glDisable(GL11.GL_LINE_SMOOTH);
			GL11.glDisable(GL11.GL_SCISSOR_TEST);

			Utils.resetLineWidth();
		}
	}

	private double getSpeed() {
		double distTraveledLastTickX = mc.thePlayer.posX - mc.thePlayer.prevPosX;
		double distTraveledLastTickZ = mc.thePlayer.posZ - mc.thePlayer.prevPosZ;
		return MathHelper.sqrt_double(distTraveledLastTickX * distTraveledLastTickX
				+ distTraveledLastTickZ * distTraveledLastTickZ);
	}

	@Override
	public String getText(boolean editMode) {
		if(graphMode) {
			return "";
		}

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
