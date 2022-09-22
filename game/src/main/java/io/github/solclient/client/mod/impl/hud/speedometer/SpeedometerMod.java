package io.github.solclient.client.mod.impl.hud.speedometer;

import java.text.DecimalFormat;

import org.lwjgl.opengl.GL11;

import com.google.gson.annotations.Expose;

import io.github.solclient.client.mod.annotation.Option;
import io.github.solclient.client.mod.hud.SimpleHudMod;
import io.github.solclient.client.platform.mc.render.GlStateManager;
import io.github.solclient.client.util.Utils;
import io.github.solclient.client.util.data.Position;
import io.github.solclient.client.util.data.Rectangle;

public class SpeedometerMod extends SimpleHudMod {

	public static final SpeedometerMod INSTANCE = new SpeedometerMod();

	private static final DecimalFormat FORMAT = new DecimalFormat("0.00");
	private static final int MAX_SPEEDS = 200;

	@Expose
	@Option
	private boolean graphMode;

	private double[] speeds = new double[MAX_SPEEDS];
	private long lastUpdate;

	@Override
	public String getId() {
		return "speedometer";
	}

	public void addSpeed(double speed) {
		System.arraycopy(speeds, 1, speeds, 0, MAX_SPEEDS - 1);
		speeds[MAX_SPEEDS - 1] = speed;
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
				speeds = new double[MAX_SPEEDS];
			}

			float[] bounds = element.getHighPrecisionMultipliedBounds();
			Utils.scissor(bounds[0], bounds[1], bounds[2], bounds[3]);
			textColour.bind();
			GlStateManager.lineWidth(1.5F);

			if(!editMode && !mc.isGamePaused()
					&& (lastUpdate == -1 || (System.currentTimeMillis() - lastUpdate) > 30)) {
				addSpeed(getSpeed());
				lastUpdate = System.currentTimeMillis();
			}

			// TODO fix this legacy opengl nightmare

			GlStateManager.enableBlend();
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GlStateManager.blendFunction(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GL11.glEnable(GL11.GL_LINE_SMOOTH);
			GL11.glShadeModel(GL11.GL_SMOOTH);
			GL11.glEnable(GL11.GL_SCISSOR_TEST);

			GL11.glBegin(GL11.GL_LINE_STRIP);

			for(int i = 0; i < MAX_SPEEDS; i++) {
				GL11.glVertex2d(position.getX() + (i * (((getBounds(position).getWidth() + 0.4) / MAX_SPEEDS))),
						position.getY() - 2 + getBounds(position).getHeight() - (speeds[i] * 16));
			}

			GL11.glEnd();
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glDisable(GL11.GL_LINE_SMOOTH);
			GL11.glDisable(GL11.GL_SCISSOR_TEST);
			GL11.glColor4f(1, 1, 1, 1);

			GlStateManager.resetLineWidth();
		}
	}

	private double getSpeed() {
		double xTraveled = mc.getPlayer().x() - mc.getPlayer().previousX();
		double zTraveled = mc.getPlayer().z() - mc.getPlayer().previousZ();
		return Math.sqrt(xTraveled * xTraveled + zTraveled * zTraveled);
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
			return FORMAT.format(getSpeed() / 0.05F) + " m/s";
		}
	}

}
