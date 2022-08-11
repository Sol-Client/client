package io.github.solclient.client.v1_19_2.mixins.platform.mc.world.level.block;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import io.github.solclient.client.platform.mc.world.level.Level;
import io.github.solclient.client.platform.mc.world.level.block.BlockPos;
import io.github.solclient.client.platform.mc.world.level.block.BlockState;
import io.github.solclient.client.platform.mc.world.level.block.BlockType;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.world.World;

@Mixin(AbstractBlock.AbstractBlockState.class)
public interface BlockStateImpl extends BlockState {

	@Override
	default @NotNull BlockType getType() {
		return (BlockType) getBlock();
	}

	@Shadow
	Block getBlock();

	@Override
	default boolean hasMenu(@NotNull Level level, @NotNull BlockPos pos) {
		return createScreenHandlerFactory((World) level, (net.minecraft.util.math.BlockPos) pos) != null;
	}

	@Shadow
	NamedScreenHandlerFactory createScreenHandlerFactory(World world, net.minecraft.util.math.BlockPos blockPos);

}
