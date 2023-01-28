package io.github.solclient.client.mod;

import io.github.solclient.client.mod.impl.SolClientMod;

/**
 * Represents a mod that cannot be disabled.
 */
public abstract class ConfigOnlyMod extends SolClientMod {

	@Override
	public boolean isEnabledByDefault() {
		return true;
	}

}
