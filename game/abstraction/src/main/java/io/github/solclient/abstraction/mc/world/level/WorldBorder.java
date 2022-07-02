package io.github.solclient.abstraction.mc.world.level;

import io.github.solclient.abstraction.mc.world.level.block.BlockPos;

public interface WorldBorder {

	boolean contains(BlockPos pos);

}
