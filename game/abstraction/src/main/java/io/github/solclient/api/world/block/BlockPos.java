package io.github.solclient.api.world.block;

import org.jetbrains.annotations.NotNull;

import io.github.solclient.api.maths.Vec3i;

public interface BlockPos extends Vec3i {

	static @NotNull BlockPos create(int x, int y, int z) {
		throw new UnsupportedOperationException();
	}

}
