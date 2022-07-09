package io.github.solclient.abstraction.mc.maths;

import org.jetbrains.annotations.NotNull;

public interface Box {

	static @NotNull Box create(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
		throw new UnsupportedOperationException();
	}

	double minX();

	double minY();

	double minZ();

	double maxX();

	double maxY();

	double maxZ();

}
