package io.github.solclient.abstraction.mc.network;

import java.util.concurrent.CompletableFuture;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import io.github.solclient.abstraction.Helper;

public interface ServerData {

	@Nullable String getName();

	@NotNull String getIp();

	@Helper
	@NotNull CompletableFuture<Integer> ping();

}
