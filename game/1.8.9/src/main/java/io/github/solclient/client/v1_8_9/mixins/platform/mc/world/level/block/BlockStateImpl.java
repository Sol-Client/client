package io.github.solclient.client.v1_8_9.mixins.platform.mc.world.level.block;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.*;

import io.github.solclient.client.platform.mc.world.level.Level;
import io.github.solclient.client.platform.mc.world.level.block.*;
import net.minecraft.block.Block;
import net.minecraft.inventory.Inventory;
import net.minecraft.world.World;

@Mixin(net.minecraft.block.BlockState.class)
public interface BlockStateImpl extends BlockState {

	@Override
	default @NotNull BlockType getType() {
		return (BlockType) getBlock();
	}

	@Shadow
	Block getBlock();

	@Override
	default boolean hasMenu(@NotNull Level level, @NotNull BlockPos pos) {
		return getBlock().hasBlockEntity()
				&& ((World) level).getBlockEntity((net.minecraft.util.math.BlockPos) pos) instanceof Inventory;
	}

}
