package io.github.solclient.client.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.client.render.BuiltChunkStorage;
import net.minecraft.client.world.BuiltChunk;
import net.minecraft.util.math.BlockPos;

@Mixin(BuiltChunkStorage.class)
public interface BuiltChunkStorageMixin {

	@Invoker("getRenderedChunk")
	public BuiltChunk getChunk(BlockPos pos);

}
