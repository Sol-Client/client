package io.github.solclient.api.world.block;

import org.jetbrains.annotations.NotNull;

import io.github.solclient.api.world.World;

public interface BlockState {

	@NotNull Block getBlock();

	boolean isOpaqueFullCube(@NotNull World world, @NotNull BlockPos pos);

}
