package io.github.solclient.client.platform.mc.world.level;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import io.github.solclient.client.platform.Helper;
import io.github.solclient.client.platform.mc.text.Text;
import io.github.solclient.client.platform.mc.world.level.block.BlockPos;
import io.github.solclient.client.platform.mc.world.level.block.BlockState;
import io.github.solclient.client.platform.mc.world.level.chunk.Chunk;
import io.github.solclient.client.platform.mc.world.level.chunk.ChunkPos;

public interface Level {

	boolean isOpaqueFullCube(@NotNull BlockPos pos);

	@NotNull BlockState getBlockState(@NotNull BlockPos pos);

	@NotNull Chunk getChunk(@NotNull ChunkPos pos);

	@NotNull Chunk getChunk(int x, int z);

	@NotNull WorldBorder getWorldBorder();

	@Helper
	@Nullable Text getScoreboardTitle();

}
