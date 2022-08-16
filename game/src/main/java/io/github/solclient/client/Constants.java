package io.github.solclient.client;

import org.apache.logging.log4j.LogManager;

import io.github.solclient.client.util.Utils;

public class Constants {

	public static final boolean DEV;
	public static final String VERSION;
	public static final String MC_VERSION;
	public static final String NAME;

	public static final String KEY_TRANSLATION_KEY = "sol_client.key";
	public static final String KEY_CATEGORY = KEY_TRANSLATION_KEY + ".category";

	static {
		String version = System.getProperty("io.github.solclient.client.version", "unknown");
		DEV = version.equals("dev");
		if(DEV) {
			VERSION = devVersion();
		}
		else {
			VERSION = version;
		}

		MC_VERSION = System.getProperty("io.github.solclient.client.mc_version");

		NAME = "Sol Client " + VERSION;
	}


	private static String devVersion() {
		try {
			return "git " + Utils.getGitBranch();
		}
		catch(Throwable error) {
			LogManager.getLogger().error("Could not determine git branch", error);
			return "dev";
		}
	}
}
