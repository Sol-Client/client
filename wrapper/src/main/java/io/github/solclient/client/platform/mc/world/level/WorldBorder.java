package io.github.solclient.client.platform.mc.world.level;

import io.github.solclient.client.platform.mc.world.level.block.BlockPos;

public interface WorldBorder {

	boolean contains(BlockPos pos);

}
