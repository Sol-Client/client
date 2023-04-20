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

package io.github.solclient.client.util.data;

import org.lwjgl.input.Keyboard;

public final class Modifier {

	public static final int CTRL = 1;
	public static final int ALT = 2;
	public static final int SHIFT = 4;

	public static boolean isModifier(int key) {
		return key == Keyboard.KEY_LCONTROL || key == Keyboard.KEY_LMENU || key == Keyboard.KEY_LSHIFT;
	}

}
