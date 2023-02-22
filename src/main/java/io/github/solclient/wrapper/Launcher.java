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
