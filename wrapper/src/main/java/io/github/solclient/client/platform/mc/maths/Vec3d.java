package io.github.solclient.client.platform.mc.maths;

import org.jetbrains.annotations.NotNull;

public interface Vec3d {

	static @NotNull Vec3d create(double x, double y, double z) {
		throw new UnsupportedOperationException();
	}

	double x();

	double y();

	double z();

	double distanceSquared(Vec3d vec);
}
