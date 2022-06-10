package io.github.solclient.api.maths;

import org.jetbrains.annotations.NotNull;

public interface Vec3i {

	static @NotNull Vec3i create(int x, int y, int z) {
		throw new UnsupportedOperationException();
	}

	int getX();

	int getY();

	int getZ();

}
