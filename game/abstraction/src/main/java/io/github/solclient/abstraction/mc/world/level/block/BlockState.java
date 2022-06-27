package io.github.solclient.abstraction.mc.world.level.block;

import org.jetbrains.annotations.NotNull;

import io.github.solclient.abstraction.mc.world.level.Level;

public interface BlockState {

	@NotNull BlockType getBlock();

	boolean isOpaqueFullCube(@NotNull Level level, @NotNull BlockPos pos);

}
