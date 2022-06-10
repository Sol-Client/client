package io.github.solclient.api.network;

import org.jetbrains.annotations.NotNull;

public interface C2SChatMessagePacket extends Packet {

	static @NotNull C2SChatMessagePacket create(String message) {
		throw new UnsupportedOperationException();
	}

}
