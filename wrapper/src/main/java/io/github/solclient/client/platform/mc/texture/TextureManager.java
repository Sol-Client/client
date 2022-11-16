package io.github.solclient.client.platform.mc.texture;

import java.util.concurrent.CompletableFuture;

import org.jetbrains.annotations.*;

import io.github.solclient.client.platform.mc.resource.Identifier;

public interface TextureManager {

	@NotNull CompletableFuture<Texture> download(@NotNull String url);

	void bind(@NotNull Texture texture);

	void bind(@NotNull Identifier id);

	void delete(@NotNull Texture texture);

	@Nullable Texture getTexture(Identifier id);

}
