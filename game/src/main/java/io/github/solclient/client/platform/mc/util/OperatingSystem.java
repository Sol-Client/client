package io.github.solclient.client.platform.mc.util;

import org.jetbrains.annotations.Nullable;

import io.github.solclient.client.platform.VirtualEnum;

public interface OperatingSystem extends VirtualEnum {

	OperatingSystem WINDOWS = null,
			LINUX = null,
			OSX = null;

	@Nullable String getTelemetryName();

}
