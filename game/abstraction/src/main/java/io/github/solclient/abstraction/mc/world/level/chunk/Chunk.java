package io.github.solclient.abstraction.mc.world.level.chunk;

import java.util.Map;

import org.jetbrains.annotations.NotNull;

import io.github.solclient.abstraction.mc.world.level.block.BlockEntity;
import io.github.solclient.abstraction.mc.world.level.block.BlockPos;

public interface Chunk {

	@NotNull Map<BlockPos, BlockEntity> getBlockEntities();

}
