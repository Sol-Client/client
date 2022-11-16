package io.github.solclient.client.platform.mc.raycast;

import org.jetbrains.annotations.*;

import io.github.solclient.client.platform.mc.Direction;
import io.github.solclient.client.platform.mc.world.entity.Entity;
import io.github.solclient.client.platform.mc.world.level.block.BlockPos;

public interface HitResult {

	@NotNull HitType getType();

	@Nullable Entity getEntity();

	@Nullable BlockPos getBlockPos();

	@Nullable Direction getBlockSide();

}
