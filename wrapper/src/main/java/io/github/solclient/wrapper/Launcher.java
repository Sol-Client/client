package io.github.solclient.wrapper;

import java.lang.invoke.*;

import org.apache.logging.log4j.LogManager;

import io.github.solclient.util.GlobalConstants;

/**
 * Loads and executes the main method of MAIN_CLASS, using ClassWrapper.
 */
public final class Launcher {

	private static final String MAIN_CLASS = "io.github.solclient.client.Premain";

	public static void main(String[] args) throws Throwable {
		if (System.getProperty("mixin.service") == null)
			System.setProperty("mixin.service", "io.github.solclient.wrapper.WrapperMixinService");

		try (ClassWrapper wrapper = new ClassWrapper(Prelaunch.prepareClasspath())) {
			// @formatter:off
			MethodHandle mainMethod = MethodHandles.lookup().findStatic(
					wrapper.loadClass(MAIN_CLASS),
					"main",
					GlobalConstants.MAIN_METHOD
			);
			// @formatter:on
			mainMethod.invokeExact(args);
		} catch (Throwable error) {
			LogManager.getLogger().error("Launch error", error);
		}
	}

}
