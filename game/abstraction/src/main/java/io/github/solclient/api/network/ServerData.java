package io.github.solclient.api.network;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ServerData {

	@Nullable String getName();

	@NotNull String getIp();

}
