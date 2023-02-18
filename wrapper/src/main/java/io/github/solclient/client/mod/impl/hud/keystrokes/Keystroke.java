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

import com.mojang.blaze3d.platform.GlStateManager;

import io.github.solclient.client.CpsMonitor;
import io.github.solclient.client.util.MinecraftUtils;
import lombok.RequiredArgsConstructor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.util.math.MathHelper;

@RequiredArgsConstructor
public class Keystroke {

	private final MinecraftClient mc = MinecraftClient.getInstance();
	private final KeystrokesMod mod;
	private final KeyBinding keyBinding;
	private final String name;
	private final int x;
	protected final int width;
	private final int height;
	private boolean wasDown;
	private long end;

	public void render(int offsetX, int offsetY) {
		int x = this.x + offsetX;
		int y = offsetY;
		boolean down = keyBinding.isPressed();
		GlStateManager.enableBlend();

		if ((wasDown && !down) || (!wasDown && down)) {
			end = System.currentTimeMillis();
		}

		float progress = 1F;

		if (mod.smoothColours) {
			progress = (System.currentTimeMillis() - end) / 100.0F;
		}

		if (down) {
			progress = 1F - progress;
		}

		progress = MathHelper.clamp(progress, 0, 1);

		if (mod.background) {
			DrawableHelper.fill(x, y, x + width, y + height, MinecraftUtils.lerpColour(mod.backgroundColourPressed.getValue(),
					mod.backgroundColour.getValue(), progress));
		}

		if (mod.border) {
			MinecraftUtils.drawOutline(x, y, x + width, y + height,
					MinecraftUtils.lerpColour(mod.borderColourPressed.getValue(), mod.borderColour.getValue(), progress));
		}

		int fgColour = MinecraftUtils.lerpColour(mod.textColourPressed.getValue(), mod.textColour.getValue(), progress);

		if (name == null) {
			DrawableHelper.fill(x + 10, y + 3, x + width - 10, y + 4, fgColour);

			if (mod.shadow)
				DrawableHelper.fill(x + 11, y + 4, x + width - 9, y + 5, MinecraftUtils.getShadowColour(fgColour));
		} else {
			if (mod.cps) {
				CpsMonitor monitor = null;

				if (name.equals("LMB"))
					monitor = CpsMonitor.LMB;
				else if (name.equals("RMB"))
					monitor = CpsMonitor.RMB;

				if (monitor != null) {
					String cpsText = monitor.getCps() + " CPS";
					float scale = 0.5F;

					GlStateManager.pushMatrix();
					GlStateManager.scale(scale, scale, scale);

					mc.textRenderer.draw(cpsText,
							(x / scale) + (width / 2F / scale) - (mc.textRenderer.getStringWidth(cpsText) / 2F),
							(y + height - (mc.textRenderer.fontHeight * scale)) / scale - 3, fgColour, mod.shadow);

					GlStateManager.popMatrix();

					y -= 3;
				}

			}

			y += 1;
			mc.textRenderer.draw(name, x + (width / 2F) - (mc.textRenderer.getStringWidth(name) / 2F),
					y + (height / 2F) - (mc.textRenderer.fontHeight / 2F), fgColour, mod.shadow);
		}
		wasDown = down;
	}

}
