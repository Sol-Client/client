package io.github.solclient.client.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.render.*;

@Mixin(WorldRenderer.class)
public interface MixinWorldRenderer {

	@Accessor
	BuiltChunkStorage getChunks();

}
