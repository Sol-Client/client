package io.github.solclient.client.platform.mc.world.level.block;

import org.jetbrains.annotations.NotNull;

import io.github.solclient.client.platform.mc.world.level.ClientLevel;
import io.github.solclient.client.platform.mc.world.level.Level;

public interface BlockState {

	@NotNull BlockType getType();

	boolean hasMenu(@NotNull Level level, @NotNull BlockPos pos);

}
