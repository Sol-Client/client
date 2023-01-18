package io.github.solclient.util;

import java.net.*;

import io.github.solclient.client.util.*;
import lombok.experimental.UtilityClass;

@UtilityClass
public class GlobalConstants {

	public final boolean DEV = Boolean.getBoolean("loader.development");
	public final String VERSION_STRING = System.getProperty("io.github.solclient.client.version", "unknown");
	public final SemVer VERSION = SemVer.parseOrNull(VERSION_STRING);
	public final String LAUNCHER = System.getProperty("io.github.solclient.client.launcher", "unknown");
	public final boolean AUTOUPDATE = Boolean.getBoolean("io.github.solclient.client.autoupdate");
	public final String NAME = "Sol Client " + VERSION_STRING;
	public final String KEY_TRANSLATION_KEY = "sol_client.key";
	public final String KEY_CATEGORY = KEY_TRANSLATION_KEY + ".category";
	public final URL RELEASE_API = sneakyParse(System.getProperty("io.github.solclient.client.release_api",
			"https://api.github.com/repos/Sol-Client/Client/releases/latest"));
	public final long DISCORD_APPLICATION = 925701938211868683L;
	// please change
	public final String IMGUR_APPLICATION = "4efd63137720136";
	// please don't remove :(
	public final String COPYRIGHT = "© 2023 TheKodeToad and contributors";

	private URL sneakyParse(String url) {
		try {
			return new URL(url);
		} catch (MalformedURLException error) {
			throw new AssertionError(error);
		}
	}

}
