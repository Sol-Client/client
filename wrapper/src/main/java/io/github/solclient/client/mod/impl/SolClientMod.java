package io.github.solclient.client.mod.impl;

import java.nio.file.Path;

import org.apache.logging.log4j.Logger;

import io.github.solclient.client.Client;
import io.github.solclient.client.mod.Mod;
import net.minecraft.client.MinecraftClient;

/**
 * Base class for built-in mods. Adds some handy stuff.
 */
public abstract class SolClientMod extends Mod {

	protected final Logger logger = getLogger();
	protected final MinecraftClient mc = MinecraftClient.getInstance();

	public String getTranslationKey(String key) {
		return "sol_client.mod." + getId() + '.' + key;
	}

	@Override
	public boolean isEnabledByDefault() {
		return false;
	}

	@Override
	public Path getConfigFolder() {
		return Client.INSTANCE.getConfigFolder();
	}

}
