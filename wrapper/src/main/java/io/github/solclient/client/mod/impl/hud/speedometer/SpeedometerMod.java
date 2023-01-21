package io.github.solclient.client.mod.impl.hud.speedometer;

import java.text.DecimalFormat;

import org.lwjgl.opengl.GL11;

import com.google.gson.annotations.Expose;
import com.mojang.blaze3d.platform.GlStateManager;

import io.github.solclient.client.mod.annotation.Option;
import io.github.solclient.client.mod.hud.SimpleHudMod;
import io.github.solclient.client.util.MinecraftUtils;
import io.github.solclient.client.util.data.*;
import net.minecraft.util.math.MathHelper;

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
		if (!graphMode) {
			return super.getBounds(position);
		}

		return super.getBounds(position).multiply(1.5F, 1.5F);
	}

	@Override
	public void render(Position position, boolean editMode) {
		super.render(position, editMode);

		if (graphMode) {
			double[] speeds = this.speeds;

			if (editMode) {
				speeds = new double[SPEED_COUNT];
			}

			float[] bounds = element.getHighPrecisionMultipliedBounds();
			MinecraftUtils.scissor(bounds[0], bounds[1], bounds[2], bounds[3]);
			textColour.bind();
			GL11.glLineWidth(1.5F);

			if (!editMode && !mc.isPaused() && (lastUpdate == -1 || (System.currentTimeMillis() - lastUpdate) > 30)) {
				addSpeed(getSpeed());
				lastUpdate = System.currentTimeMillis();
			}

			GlStateManager.enableBlend();
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GL11.glEnable(GL11.GL_LINE_SMOOTH);
			GL11.glShadeModel(GL11.GL_SMOOTH);
			GL11.glEnable(GL11.GL_SCISSOR_TEST);

			GL11.glBegin(GL11.GL_LINE_STRIP);

			for (int i = 0; i < SPEED_COUNT; i++) {
				GL11.glVertex2d(
						position.getX() + (i * (((getBounds(position).getWidth() + 0.4) / (double) SPEED_COUNT))),
						position.getY() - 2 + getBounds(position).getHeight() - (speeds[i] * 16));
			}

			GL11.glEnd();
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glDisable(GL11.GL_LINE_SMOOTH);
			GL11.glDisable(GL11.GL_SCISSOR_TEST);
			GlStateManager.color(1, 1, 1, 1);

			MinecraftUtils.resetLineWidth();
		}
	}

	private double getSpeed() {
		double xTraveled = mc.player.x - mc.player.prevX;
		double zTraveled = mc.player.z - mc.player.prevZ;
		return MathHelper.sqrt(xTraveled * xTraveled + zTraveled * zTraveled);
	}

	@Override
	public String getText(boolean editMode) {
		if (graphMode) {
			return "";
		}

		if (editMode) {
			return "0.00 m/s";
		} else {
			return FORMAT.format(getSpeed() / 0.05F) + " m/s";
		}
	}

}
