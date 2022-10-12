package io.github.solclient.client.v1_8_9.mixins.platform.mc.maths;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.*;

import io.github.solclient.client.platform.mc.maths.Vec3i;

@Mixin(net.minecraft.util.math.Vec3i.class)
public abstract class Vec3iImpl implements Vec3i {

	@Shadow
	private @Final int x, y, z;

	@Override
	public int x() {
		return x;
	}

	@Override
	public int y() {
		return y;
	}

	@Override
	public int z() {
		return z;
	}

	@Override
	public double distanceSquared(double x, double y, double z) {
		return squaredDistanceTo(x, y, z);
	}

	@Shadow
	public abstract double squaredDistanceTo(final double x, final double y, final double z);

	@Override
	public double distanceSquared(@NotNull Vec3i other) {
		return getSquaredDistance((net.minecraft.util.math.Vec3i) other);
	}

	@Shadow
	public abstract double getSquaredDistance(net.minecraft.util.math.Vec3i vec);

}
