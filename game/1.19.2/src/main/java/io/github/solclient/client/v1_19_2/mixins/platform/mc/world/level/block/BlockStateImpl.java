package io.github.solclient.client.v1_19_2.mixins.platform.mc.world.level.block;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.*;

import io.github.solclient.client.platform.mc.world.level.Level;
import io.github.solclient.client.platform.mc.world.level.block.*;
import io.github.solclient.client.platform.mc.world.level.block.BlockState;
import net.minecraft.block.*;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.world.World;

@Mixin(AbstractBlock.AbstractBlockState.class)
public abstract class BlockStateImpl implements BlockState {

	@Override
	public @NotNull BlockType getType() {
		return (BlockType) getBlock();
	}

	@Shadow
	public abstract Block getBlock();

	@Override
	public boolean hasMenu(@NotNull Level level, @NotNull BlockPos pos) {
		return createScreenHandlerFactory((World) level, (net.minecraft.util.math.BlockPos) pos) != null;
	}

	@Shadow
	public abstract NamedScreenHandlerFactory createScreenHandlerFactory(World world, net.minecraft.util.math.BlockPos blockPos);

}
