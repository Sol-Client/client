package io.github.solclient.gradle;

import com.github.glassmc.kiln.standard.environment.Environment;

import java.io.File;
import java.util.Arrays;

public class SolClientKilnEnvironment implements Environment {

	@Override
	public String getMainClass() {
		return "net.minecraft.launchwrapper.Launch";
	}

	@Override
	public String[] getProgramArguments(String environment, String version) {
		return new String[] { "--tweakClass", "io.github.solclient.client.tweak.Tweaker" };
	}

	@Override
	public String[] getRuntimeDependencies(File file) {
		return new String[0];
	}

	@Override
	public String getVersion(String mcVersion) {
		return "Sol-Client-" + mcVersion;
	}

}
