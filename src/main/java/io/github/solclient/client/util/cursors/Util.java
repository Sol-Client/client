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

import java.io.*;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.*;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.InputImplementation;

final class Util {

	private static final Logger LOGGER = LogManager.getLogger();
	private static MethodHandle getInputImplementationMethod;
	private static MethodHandle getDisplayImplementationMethod;

	static {
		try {
			Method getInputImplementation = Mouse.class.getDeclaredMethod("getImplementation");
			getInputImplementation.setAccessible(true);
			getInputImplementationMethod = MethodHandles.lookup().unreflect(getInputImplementation);

			Method getDisplayImplementation = Display.class.getDeclaredMethod("getImplementation");
			getDisplayImplementation.setAccessible(true);
			getDisplayImplementationMethod = MethodHandles.lookup().unreflect(getDisplayImplementation);
		} catch (Throwable error) {
			LOGGER.error("Could not perform reflection", error);
		}
	}

	public static RuntimeException sneakyThrow(Throwable error) {
		if (error instanceof Error)
			throw (Error) error;
		if (error instanceof RuntimeException)
			throw (RuntimeException) error;
		if (error instanceof IOException)
			throw new UncheckedIOException((IOException) error);

		throw new IllegalStateException(error);
	}

	public static boolean foundInputImplementationMethod() {
		return getInputImplementationMethod != null;
	}

	public static InputImplementation getInputImplementation() {
		try {
			return (InputImplementation) getInputImplementationMethod.invokeExact();
		} catch (Throwable error) {
			throw sneakyThrow(error);
		}
	}

	public static Object getDisplayImplementation() {
		try {
			return getDisplayImplementationMethod.invoke();
		} catch (Throwable error) {
			throw sneakyThrow(error);
		}
	}

	public static void loadLibrary(String name) throws IOException {
		String resourceName = System.mapLibraryName(name);
		File file = File.createTempFile(resourceName, "");
		file.deleteOnExit();
		try (InputStream in = Util.class.getResourceAsStream('/' + resourceName)) {
			FileUtils.copyInputStreamToFile(in, file);
		}
		System.load(file.getAbsolutePath());
	}

}
