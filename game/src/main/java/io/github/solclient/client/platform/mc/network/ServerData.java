package io.github.solclient.client.platform.mc.network;

import java.util.concurrent.CompletableFuture;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import io.github.solclient.client.platform.Helper;

public interface ServerData {

	@Nullable String getName();

	@NotNull String getIp();

	@Helper
	@NotNull CompletableFuture<Integer> ping();

}
