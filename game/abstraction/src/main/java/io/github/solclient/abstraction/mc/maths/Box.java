package io.github.solclient.abstraction.mc.maths;

public interface Box {

	static Box create(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
		throw new UnsupportedOperationException();
	}

	double minX();

	double minY();

	double minZ();

	double maxX();

	double maxY();

	double maxZ();

}
