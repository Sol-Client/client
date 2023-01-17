package io.github.solclient.client;

import java.net.URL;

import io.github.solclient.client.util.*;

public final class GlobalConstants {

	public static final String VERSION_STRING = System.getProperty("io.github.solclient.client.version", "unknown");
	public static final SemVer VERSION = SemVer.parseOrNull(VERSION_STRING);
	public static final String LAUNCHER = System.getProperty("io.github.solclient.client.launcher", "unknown");
	public static final boolean AUTOUPDATE = Boolean.getBoolean("io.github.solclient.client.autoupdate");
	public static final String NAME = "Sol Client " + VERSION_STRING;
	public static final String KEY_TRANSLATION_KEY = "sol_client.key";
	public static final String KEY_CATEGORY = KEY_TRANSLATION_KEY + ".category";
	public static final URL RELEASE_API = Utils.sneakyParse(System.getProperty("io.github.solclient.client.release_api",
			"https://api.github.com/repos/Sol-Client/Client/releases/latest"));
	public static final long DISCORD_APPLICATION = 925701938211868683L;
	// please change
	public static final String IMGUR_APPLICATION = "4efd63137720136";
	// please don't remove :(
	public static final String COPYRIGHT = "© 2023 TheKodeToad and contributors";

}
