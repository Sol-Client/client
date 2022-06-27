package io.github.solclient.abstraction.mc.network;

import org.jetbrains.annotations.NotNull;

public interface C2SChatMessagePacket extends Packet {

	static @NotNull C2SChatMessagePacket create(String message) {
		throw new UnsupportedOperationException();
	}

	String message();

}
