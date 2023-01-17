package io.github.solclient.wrapper;

import java.lang.invoke.*;

/**
 * Loads and executes the main method of MAIN_CLASS, using ClassWrapper.
 */
public final class Springboard {

	private static final String MAIN_CLASS = "io.github.solclient.client.Premain";
	private static final MethodType MAIN_METHOD = MethodType.methodType(void.class, String[].class);

	public static void main(String[] args) throws Throwable {
		// @formatter:off
		MethodHandle mainMethod = MethodHandles.lookup().findStatic(
				ClassWrapper.INSTANCE.loadClass(MAIN_CLASS),
				"main",
				MAIN_METHOD
		);
		// @formatter:on
		mainMethod.invokeExact(args);
	}

}
