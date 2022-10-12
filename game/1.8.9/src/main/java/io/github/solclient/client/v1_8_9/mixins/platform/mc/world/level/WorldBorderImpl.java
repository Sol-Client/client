package io.github.solclient.client.v1_8_9.mixins.platform.mc.world.level;

import org.spongepowered.asm.mixin.*;

import io.github.solclient.client.platform.mc.world.level.WorldBorder;
import io.github.solclient.client.platform.mc.world.level.block.BlockPos;

@Mixin(net.minecraft.world.border.WorldBorder.class)
@Implements(@Interface(iface = WorldBorder.class, prefix = "platform$"))
public abstract class WorldBorderImpl {

	public boolean platform$contains(BlockPos pos) {
		return contains((net.minecraft.util.math.BlockPos) pos);
	}

	@Shadow
	public abstract boolean contains(net.minecraft.util.math.BlockPos pos);

}
