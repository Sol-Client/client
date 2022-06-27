package io.github.solclient.abstraction.mc.texture;

import java.util.concurrent.CompletableFuture;

import org.jetbrains.annotations.NotNull;

import io.github.solclient.abstraction.mc.Identifier;

public interface TextureManager {

	@NotNull CompletableFuture<Texture> download(@NotNull String url);

	void bind(@NotNull Texture texture);

	void bind(@NotNull Identifier id);

	void delete(@NotNull Texture texture);

}
