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
