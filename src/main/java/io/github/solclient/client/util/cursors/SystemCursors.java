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

package io.github.solclient.client.util.cursors;

import org.lwjgl.LWJGLException;
import org.lwjgl.LWJGLUtil;
import org.lwjgl.input.Mouse;

public final class SystemCursors {

	public static final byte ARROW = 0;
	public static final byte IBEAM = 1;
	public static final byte CROSSHAIR = 2;
	public static final byte POINTING_HAND = 3;
	public static final byte RESIZE_EW = 4;
	public static final byte RESIZE_NS = 5;
	public static final byte RESIZE_NWSE = 6;
	public static final byte RESIZE_NESW = 7;
	public static final byte ALL_CURSOR = 8;
	public static final byte NOT_ALLOWED = 9;
	public static final byte SIZE = size();

	public static void setCursor(byte cursor) throws LWJGLException {
		if (cursor == ARROW) {
			Mouse.setNativeCursor(null);
			return;
		}

		if (cursor < 0 || cursor >= SIZE)
			throw new IllegalArgumentException(Byte.toString(cursor));

		if (LWJGLUtil.getPlatform() == LWJGLUtil.PLATFORM_LINUX)
			X11SystemCursors.setCursor(cursor);
		else if (LWJGLUtil.getPlatform() == LWJGLUtil.PLATFORM_WINDOWS)
			Win32SystemCursors.setCursor(cursor);
	}

	private static byte size() {
		return NOT_ALLOWED + 1;
	}

}
