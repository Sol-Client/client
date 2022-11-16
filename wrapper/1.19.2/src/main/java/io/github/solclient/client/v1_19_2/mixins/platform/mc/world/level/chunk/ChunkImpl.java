package io.github.solclient.client.v1_19_2.mixins.platform.mc.world.level.chunk;

import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.*;

import io.github.solclient.client.platform.mc.world.level.block.*;
import io.github.solclient.client.platform.mc.world.level.chunk.Chunk;

@Mixin(net.minecraft.world.chunk.Chunk.class)
public class ChunkImpl implements Chunk {

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public @NotNull Map<BlockPos, BlockEntity> getBlockEntityMap() {
		return (Map) blockEntities;
	}

	@Shadow
	protected @Final Map<net.minecraft.util.math.BlockPos, net.minecraft.block.entity.BlockEntity> blockEntities;

}
