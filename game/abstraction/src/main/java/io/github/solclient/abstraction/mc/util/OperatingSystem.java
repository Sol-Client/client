package io.github.solclient.abstraction.mc.util;

import org.jetbrains.annotations.Nullable;

import io.github.solclient.abstraction.VirtualEnum;

public interface OperatingSystem extends VirtualEnum {

	OperatingSystem WINDOWS = null;
	OperatingSystem LINUX = null;
	OperatingSystem OSX = null;

	@Nullable String getTelemetryName();

}
