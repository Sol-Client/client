package io.github.solclient.api.world;

import org.jetbrains.annotations.NotNull;

import io.github.solclient.api.world.block.BlockPos;
import io.github.solclient.api.world.block.BlockState;

public interface World {

	boolean isOpaqueFullCube(@NotNull BlockPos pos);

	@NotNull BlockState getBlockState(@NotNull BlockPos pos);

}
