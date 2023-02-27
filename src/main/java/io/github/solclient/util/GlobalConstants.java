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

package io.github.solclient.util;

import java.lang.invoke.MethodType;
import java.net.URL;

import lombok.experimental.UtilityClass;

@UtilityClass
public class GlobalConstants {

	// properties
	public final boolean DEV = Boolean.getBoolean("loader.development");
	// shhh... fake constant
	public static boolean optifine = Boolean.getBoolean("io.github.solclient.wrapper.optifine");
	public final boolean NO_LAUNCHER_WARNINGS = Boolean.getBoolean("io.github.solclient.wrapper.no_launcher_warnings");
	public final String VERSION_STRING = "%VERSION%";
	public final String USER_AGENT = "Sol Client/" + GlobalConstants.VERSION;
	public final SemVer VERSION = SemVer.parseOrNull(VERSION_STRING);
	public final String LAUNCHER = System.getProperty("io.github.solclient.client.launcher", "unknown");
	public final boolean AUTOUPDATE = Boolean.getBoolean("io.github.solclient.client.autoupdate");
	public final String NAME = "Sol Client " + VERSION_STRING;

	// constants
	public final String KEY_TRANSLATION_KEY = "sol_client.key";
	public final String KEY_CATEGORY = KEY_TRANSLATION_KEY + ".category";
	public final URL RELEASE_API = Utils.sneakyParse(System.getProperty("io.github.solclient.client.release_api",
			"https://api.github.com/repos/Sol-Client/Client/releases/latest"));
	public final long DISCORD_APPLICATION = 925701938211868683L;
	// please change
	public final String IMGUR_APPLICATION = "4efd63137720136";
	// please don't remove :(
	public final String COPYRIGHT = "© 2023 TheKodeToad and contributors";
	public final String OPTIFINE_JAR = "OptiFine_1.8.9_HD_U_M5";
	public final String API = "https://thekodetoad.duckdns.org:8080/v1";

	// utils
	public final MethodType MAIN_METHOD = MethodType.methodType(void.class, String[].class);

}
