package io.github.solclient.client.v1_8_9.mixins.platform.mc.world.level.chunk;

import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import io.github.solclient.client.platform.mc.world.level.block.BlockEntity;
import io.github.solclient.client.platform.mc.world.level.block.BlockPos;
import io.github.solclient.client.platform.mc.world.level.chunk.Chunk;

@Mixin(net.minecraft.world.chunk.Chunk.class)
@Implements(@Interface(iface = Chunk.class, prefix = "platform$"))
public class ChunkImpl {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public @NotNull Map<BlockPos, BlockEntity> platform$getBlockEntityMap() {
		return (Map) blockEntities;
	}

	@Shadow
	protected @Final Map<net.minecraft.util.math.BlockPos, net.minecraft.block.entity.BlockEntity> blockEntities;

}
