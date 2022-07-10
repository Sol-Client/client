package io.github.solclient.abstraction.mc.raycast;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import io.github.solclient.abstraction.mc.Direction;
import io.github.solclient.abstraction.mc.world.entity.Entity;
import io.github.solclient.abstraction.mc.world.level.block.BlockPos;

public interface HitResult {

	@NotNull HitType getType();

	@Nullable Entity getEntity();

	@Nullable BlockPos getBlockPos();

	@Nullable Direction getBlockSide();

}
