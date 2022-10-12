package io.github.solclient.client.platform.mc.world.level.chunk;

import java.util.Map;

import org.jetbrains.annotations.NotNull;

import io.github.solclient.client.platform.mc.world.level.block.*;

public interface Chunk {

	@NotNull Map<BlockPos, BlockEntity> getBlockEntityMap();

}
