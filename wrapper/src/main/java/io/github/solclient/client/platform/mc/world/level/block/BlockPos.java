package io.github.solclient.client.platform.mc.world.level.block;

import org.jetbrains.annotations.NotNull;

import io.github.solclient.client.platform.mc.maths.Vec3i;

public interface BlockPos extends Vec3i {

	static @NotNull BlockPos create(double x, double y, double z) {
		throw new UnsupportedOperationException();
	}

}
