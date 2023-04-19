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

package io.github.solclient.client.mod.impl.scrollabletooltips;

import org.lwjgl.input.Mouse;

import com.google.gson.annotations.Expose;

import io.github.solclient.client.mod.*;
import io.github.solclient.client.mod.impl.StandardMod;
import io.github.solclient.client.mod.option.annotation.*;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resource.language.I18n;

public class ScrollableTooltipsMod extends StandardMod {

	public static ScrollableTooltipsMod instance;
	public static boolean enabled;

	@Expose
	@Option
	@Slider(min = 0.5F, max = 5, step = 0.5F)
	private float scrollSensitivity = 1;
	@Expose
	@Option
	private boolean reverse;

	public int offsetX;
	public int offsetY;

	@Override
	public String getDetail() {
		return I18n.translate("sol_client.mod.screen.by", "moehreag"); // maybe also add original creator
	}

	@Override
	public void init() {
		super.init();
		instance = this;
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

	public void onRenderTooltip() {
		if (!isEnabled()) {
			return;
		}

		int wheel = Mouse.getDWheel();

		if (wheel != 0) {
			onScroll(wheel > 0);
		}
	}

	public void onScroll(boolean direction) {
		int scrollStep = (int) (12 * this.scrollSensitivity);

		if (direction) {
			scrollStep = -scrollStep;
		}

		if (!reverse) {
			scrollStep = -scrollStep;
		}

		if (Screen.hasShiftDown()) {
			offsetX += scrollStep;
		} else {
			offsetY += scrollStep;
		}
	}

	public void resetScroll() {
		offsetX = offsetY = 0;
	}

}
