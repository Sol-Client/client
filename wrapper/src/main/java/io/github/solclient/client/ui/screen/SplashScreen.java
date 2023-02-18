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

package io.github.solclient.client.ui.screen;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.Window;

public class SplashScreen {

	private static final int FG = 0xFFDF242F;
	private static final int BG = 0xFF000000;

	public static final SplashScreen INSTANCE = new SplashScreen();

	private static final int STAGES = 18;

	private final MinecraftClient mc = MinecraftClient.getInstance();
	private int stage;

	public void reset() {
		stage = 0;
	}

	public void draw() {
		if (stage > STAGES) {
			throw new IndexOutOfBoundsException(Integer.toString(stage));
		}

		Window window = new Window(mc);
		int factor = window.getScaleFactor();

		DrawableHelper.fill(0, window.getHeight() * factor - 30, window.getWidth() * factor,
				window.getHeight() * factor, BG);
		DrawableHelper.fill(0, window.getHeight() * factor - 30, window.getWidth() * factor / STAGES * stage,
				window.getHeight() * factor, FG);
		stage++;
	}

}
