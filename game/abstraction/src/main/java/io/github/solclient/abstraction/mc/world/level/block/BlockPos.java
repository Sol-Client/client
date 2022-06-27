package io.github.solclient.abstraction.mc.world.level.block;

import org.jetbrains.annotations.NotNull;

import io.github.solclient.abstraction.mc.maths.Vec3i;

public interface BlockPos extends Vec3i {

	static @NotNull BlockPos create(int x, int y, int z) {
		throw new UnsupportedOperationException();
	}

	double distanceSquared(double x, double y, double z);

	double distanceSquared(@NotNull Vec3i other);

}
