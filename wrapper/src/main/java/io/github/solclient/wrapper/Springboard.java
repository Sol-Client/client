package io.github.solclient.wrapper;

import java.lang.invoke.*;
import java.net.URL;

import net.minecraft.client.main.Main;

/**
 * Loads and executes the main method of MAIN_CLASS, using ClassWrapper.
 */
public final class Springboard {

	private static final String MAIN_CLASS = "io.github.solclient.client.Premain";
	private static final MethodType MAIN_METHOD = MethodType.methodType(void.class, String[].class);

	public static void main(String[] args) throws Throwable {
		if (System.getProperty("mixin.service") == null)
			System.setProperty("mixin.service", "io.github.solclient.wrapper.WrapperMixinService");

		try (ClassWrapper wrapper = new ClassWrapper(new URL[0])) {
			// @formatter:off
			MethodHandle mainMethod = MethodHandles.lookup().findStatic(
					wrapper.loadClass(MAIN_CLASS),
					"main",
					MAIN_METHOD
			);
			// @formatter:on
			mainMethod.invokeExact(args);
		}
	}

}
