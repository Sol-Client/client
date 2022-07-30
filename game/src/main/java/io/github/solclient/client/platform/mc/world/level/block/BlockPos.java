package io.github.solclient.client.platform.mc.world.level.block;

import org.jetbrains.annotations.NotNull;

import io.github.solclient.client.platform.mc.maths.Vec3i;

public interface BlockPos extends Vec3i {

	static @NotNull BlockPos create(int x, int y, int z) {
		throw new UnsupportedOperationException();
	}

}
