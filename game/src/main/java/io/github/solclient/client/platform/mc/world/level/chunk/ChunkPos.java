package io.github.solclient.client.platform.mc.world.level.chunk;

import org.jetbrains.annotations.NotNull;

public interface ChunkPos {

	static @NotNull ChunkPos create(int x, int z) {
		throw new UnsupportedOperationException();
	}

	int x();

	int z();

}
