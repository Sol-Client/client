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

package io.github.solclient.client.util;

import java.io.IOException;

import org.lwjgl.nanovg.*;

import lombok.Getter;

public class NanoVGManager {

	@Getter
	protected static long nvg;
	@Getter
	protected static Font regularFont;

	public static void createContext() throws IOException {
		nvg = NanoVGGL2.nvgCreate(NanoVGGL2.NVG_ANTIALIAS);
		if (nvg == 0)
			throw new IllegalStateException("NanoVG could not be initialised");
		regularFont = new Font(nvg, NanoVGManager.class.getResourceAsStream("/fonts/Inter-Regular.ttf"));
	}

	public static void closeContext() {
		regularFont.close();
	}

}
