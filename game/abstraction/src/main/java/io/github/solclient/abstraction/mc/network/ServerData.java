package io.github.solclient.abstraction.mc.network;

import java.util.concurrent.CompletableFuture;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ServerData {

	@Nullable String getName();

	@NotNull String getIp();

	@NotNull CompletableFuture<Integer> ping();

}
