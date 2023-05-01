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
