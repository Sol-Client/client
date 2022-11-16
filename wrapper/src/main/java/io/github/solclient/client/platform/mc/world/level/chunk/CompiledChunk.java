package io.github.solclient.client.platform.mc.world.level.chunk;

import org.jetbrains.annotations.NotNull;

import io.github.solclient.client.platform.mc.world.level.block.BlockPos;

public interface CompiledChunk {

	@NotNull BlockPos getPos();

}
