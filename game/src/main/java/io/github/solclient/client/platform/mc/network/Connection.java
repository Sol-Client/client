package io.github.solclient.client.platform.mc.network;

import java.util.UUID;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Connection {

	void sendPacket(Packet packet);

	@Nullable PlayerListEntry getPlayerListEntry(@NotNull UUID uuid);

}
