package io.github.solclient.abstraction.mc.world.level;

import org.jetbrains.annotations.NotNull;

import io.github.solclient.abstraction.mc.world.level.block.BlockPos;
import io.github.solclient.abstraction.mc.world.level.block.BlockState;
import io.github.solclient.abstraction.mc.world.level.chunk.Chunk;
import io.github.solclient.abstraction.mc.world.level.chunk.ChunkPos;

public interface Level {

	boolean isOpaqueFullCube(@NotNull BlockPos pos);

	@NotNull BlockState getBlockState(@NotNull BlockPos pos);

	@NotNull Chunk getChunk(@NotNull ChunkPos pos);

	@NotNull Chunk getChunk(int x, int z);

}
