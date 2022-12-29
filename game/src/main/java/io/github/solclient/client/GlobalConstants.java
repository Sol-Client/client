package io.github.solclient.client;

public class GlobalConstants {

	public static final String VERSION = System.getProperty("io.github.solclient.client.version", "DEVELOPMENT TEST");
	public static final String LAUNCHER = System.getProperty("io.github.solclient.client.launcher", "unknown");
	public static final boolean AUTOUPDATE = Boolean.getBoolean("io.github.solclient.client.autoupdate");
	public static final String NAME = "Sol Client " + VERSION;
	public static final String KEY_TRANSLATION_KEY = "sol_client.key";
	public static final String KEY_CATEGORY = KEY_TRANSLATION_KEY + ".category";

}
