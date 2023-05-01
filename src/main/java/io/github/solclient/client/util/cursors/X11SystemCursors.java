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

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;

import org.apache.logging.log4j.*;
import org.lwjgl.LWJGLException;

final class X11SystemCursors {

	private static final Logger LOGGER = LogManager.getLogger();
	private static final Long[] CACHE = new Long[SystemCursors.SIZE];
	private static MethodHandle getDisplayMethod;
	private static boolean supported = true;

	static {
		if (Util.foundInputImplementationMethod()) {
			try {
				Class<?> linuxDisplay = Class.forName("org.lwjgl.opengl.LinuxDisplay");
				Method getDisplay = linuxDisplay.getDeclaredMethod("getDisplay");
				getDisplay.setAccessible(true);
				getDisplayMethod = MethodHandles.lookup().unreflect(getDisplay);

				Util.loadLibrary("lwjglLegacyCursorsX11");
			} catch (Throwable error) {
				LOGGER.error("Could not perform reflection/load natives", error);
				supported = false;
			}
		} else
			supported = false;
	}

	public static void setCursor(byte cursor) throws LWJGLException {
		if (!supported)
			return;

		Util.getInputImplementation().setNativeCursor(getDefaultCursorHandle(cursor));
	}

	private static long getDefaultCursorHandle(byte cursor) {
		if (CACHE[cursor] != null)
			return CACHE[cursor];

		return CACHE[cursor] = nGetDefaultCursorHandle(getDisplay(), cursor);
	}

	private static long getDisplay() {
		try {
			return (long) getDisplayMethod.invokeExact();
		} catch (Throwable error) {
			throw Util.sneakyThrow(error);
		}
	}

	private static native long nGetDefaultCursorHandle(long display, byte cursor);

}
