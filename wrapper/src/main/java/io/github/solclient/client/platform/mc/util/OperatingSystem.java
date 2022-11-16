package io.github.solclient.client.platform.mc.util;

import io.github.solclient.client.platform.VirtualEnum;

public interface OperatingSystem extends VirtualEnum {

	OperatingSystem LINUX = get("LINUX"),
			SOLARIS = get("SOLARIS"),
			WINDOWS = get("WINDOWS"),
			OSX = get("OSX"),
			UNKNOWN = get("UNKNOWN");

	static OperatingSystem get(String name) {
		throw new UnsupportedOperationException();
	}

}
