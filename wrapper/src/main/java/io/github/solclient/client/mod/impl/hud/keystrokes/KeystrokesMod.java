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

package io.github.solclient.client.mod.impl.hud.keystrokes;

import org.lwjgl.nanovg.NanoVG;

import com.google.gson.annotations.Expose;

import io.github.solclient.client.event.EventHandler;
import io.github.solclient.client.event.impl.*;
import io.github.solclient.client.mod.impl.*;
import io.github.solclient.client.mod.option.annotation.*;
import io.github.solclient.client.util.*;
import io.github.solclient.client.util.data.*;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.util.math.MathHelper;

public class KeystrokesMod extends SolClientHudMod {

	@Expose
	@Option
	private boolean movement = true;
	@Expose
	@Option
	private boolean mouse = true;
	@Expose
	@Option
	private boolean mouseMovement;
	@Expose
	@Option
	private boolean showSpace;
	@Expose
	@Option
	protected boolean cps;
	@Expose
	@Option(translationKey = SolClientSimpleHudMod.TRANSLATION_KEY)
	protected boolean background = true;
	@Expose
	@ColourKey(ColourKey.BACKGROUND_COLOUR)
	@Option(translationKey = SolClientSimpleHudMod.TRANSLATION_KEY)
	protected Colour backgroundColour = new Colour(0, 0, 0, 100);
	@Expose
	@Option
	protected Colour backgroundColourPressed = new Colour(255, 255, 255, 100);
	@Expose
	@Option(translationKey = SolClientSimpleHudMod.TRANSLATION_KEY)
	protected boolean border = false;
	@Expose
	@ColourKey(ColourKey.BORDER_COLOUR)
	@Option(translationKey = SolClientSimpleHudMod.TRANSLATION_KEY)
	protected Colour borderColour = Colour.BLACK;
	@Expose
	@Option
	protected Colour borderColourPressed = Colour.WHITE;
	@Expose
	@ColourKey(ColourKey.TEXT_COLOUR)
	@Option(translationKey = SolClientSimpleHudMod.TRANSLATION_KEY)
	protected Colour textColour = Colour.WHITE;
	@Expose
	@Option
	protected Colour textColourPressed = Colour.BLACK;
	@Expose
	@Option(translationKey = SolClientSimpleHudMod.TRANSLATION_KEY)
	protected boolean shadow = false;
	@Expose
	@Option
	protected boolean smoothColours = true;

	private float mouseX;
	private float mouseY;
	private float lastMouseX;
	private float lastMouseY;

	private Keystroke w, a, s, d, lmb, rmb, space;

	@Override
	public String getId() {
		return "keystrokes";
	}

	@Override
	public void lateInit() {
		super.lateInit();
		w = new Keystroke(this, mc.options.forwardKey, "W", 18, 17, 17);
		a = new Keystroke(this, mc.options.leftKey, "A", 0, 17, 17);
		s = new Keystroke(this, mc.options.backKey, "S", 18, 17, 17);
		d = new Keystroke(this, mc.options.rightKey, "D", 36, 17, 17);
		lmb = new Keystroke(this, mc.options.attackKey, "LMB", 0, 26, 17);
		rmb = new Keystroke(this, mc.options.useKey, "RMB", 27, 26, 17);
		space = new Keystroke(this, mc.options.jumpKey, null, 0, 53, 8);
	}

	@Override
	public Rectangle getBounds(Position position) {
		int height = 0;

		if (movement)
			height += 36;
		if (mouse)
			height += 18;
		if (mouseMovement)
			height += 36;
		if (showSpace)
			height += 9;

		height--;

		return new Rectangle(position.getX(), position.getY(), 53, height);
	}

	@EventHandler
	public void setAngles(PlayerHeadRotateEvent event) {
		mouseX += event.yaw / 40F;
		mouseY -= event.pitch / 40F;
		mouseX = MathHelper.clamp(mouseX, -(space.width / 2) + 5, space.width / 2 - 4);
		mouseY = MathHelper.clamp(mouseY, -34 / 2 + 5, 34 / 2 - 5);
	}

	@EventHandler
	public void onTick(PreTickEvent event) {
		lastMouseX = mouseX;
		lastMouseY = mouseY;
		mouseX *= 0.75F;
		mouseY *= 0.75F;
	}

	@Override
	public void render(Position position, boolean editMode) {
		int x = position.getX();
		int y = position.getY();
		float tickDelta = MinecraftUtils.getTickDelta();

		if (movement) {
			w.render(x, y);
			y += 18;
			a.render(x, y);
			s.render(x, y);
			d.render(x, y);
			y += 18;
		}

		if (mouseMovement) {
			if (background)
				DrawableHelper.fill(x, y, x + space.width, y + 34, backgroundColour.getValue());

			if (border)
				MinecraftUtils.drawOutline(x, y, x + space.width, y + 34, borderColour.getValue());

			int movementY = y;
			MinecraftUtils.withNvg(() -> {
				long nvg = NanoVGManager.getNvg();

				NanoVG.nvgScale(nvg, getScale(), getScale());

				float calculatedMouseX = (lastMouseX + ((mouseX - lastMouseX) * tickDelta));
				float calculatedMouseY = (lastMouseY + ((mouseY - lastMouseY) * tickDelta));

				NanoVG.nvgBeginPath(nvg);
				NanoVG.nvgCircle(nvg, x + space.width / 2, movementY + 34 / 2, 1);
				NanoVG.nvgFillColor(nvg, textColour.nvg());
				NanoVG.nvgFill(nvg);

				NanoVG.nvgBeginPath(nvg);
				NanoVG.nvgCircle(nvg, x + space.width / 2 + calculatedMouseX, movementY + 34 / 2 + calculatedMouseY, 5);
				NanoVG.nvgStrokeWidth(nvg, 1);
				NanoVG.nvgStrokeColor(nvg, textColour.nvg());
				NanoVG.nvgStroke(nvg);
			}, true);

			y += 35;
		}

		if (mouse) {
			lmb.render(x, y);
			rmb.render(x, y);
			y += 18;
		}

		if (showSpace)
			space.render(x, y);
	}

}
