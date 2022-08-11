package io.github.solclient.client.v1_19_2.mixins.platform.mc.world.level.block;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import io.github.solclient.client.platform.mc.world.level.block.BlockPos;

public abstract class BlockPosImpl {

}

@Mixin(BlockPos.class)
interface BlockPosImpl$Static {

	@Overwrite(remap = false)
	static BlockPos create(int x, int y, int z) {
		return (BlockPos) new net.minecraft.util.math.BlockPos(x, y, z);
	}

}