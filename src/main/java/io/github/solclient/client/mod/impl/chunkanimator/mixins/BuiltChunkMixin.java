package io.github.solclient.client.mod.impl.chunkanimator.mixins;

import org.spongepowered.asm.mixin.Mixin;

import io.github.solclient.client.mod.impl.chunkanimator.BuiltChunkData;
import lombok.*;
import net.minecraft.client.world.BuiltChunk;

@Getter
@Setter
@Mixin(BuiltChunk.class)
public class BuiltChunkMixin implements BuiltChunkData {

	private long animationStart = -1;
	private boolean animationComplete;

	@Override
	public void skipAnimation() {
		animationComplete = true;
	}

}
