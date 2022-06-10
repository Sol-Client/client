package io.github.solclient.api;

import org.jetbrains.annotations.NotNull;

/**
 * Represents the current Minecraft version.
 */
public interface Version {

	static Version current() {
		throw new UnsupportedOperationException();
	}

	@NotNull String getId();

	@NotNull String getReleaseTarget();

	int getProtocolVersion();

	int getPackVersion();

	boolean isStable();

}
