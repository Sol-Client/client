/*
 * Sol Client - an open source Minecraft client
 * Copyright (C) 2021-2023  TheKodeToad and Contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package io.github.solclient.client.mod.impl.hud.speedometer;

import java.text.DecimalFormat;

import org.lwjgl.opengl.GL11;

import com.google.gson.annotations.Expose;
import com.mojang.blaze3d.platform.GlStateManager;

import io.github.solclient.client.mod.impl.SolClientSimpleHudMod;
import io.github.solclient.client.mod.option.annotation.Option;
import io.github.solclient.client.util.MinecraftUtils;
import io.github.solclient.client.util.data.*;
import net.minecraft.util.math.MathHelper;

public class SpeedometerMod extends SolClientSimpleHudMod {

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

			Rectangle bounds = element.getMultipliedBounds();
			MinecraftUtils.scissor(bounds);
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
						position.getX() + (i * (((getBounds(position).getWidth() + 0.4) / SPEED_COUNT))),
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
