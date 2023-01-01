package io.github.solclient.client.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.solclient.client.Client;
import io.github.solclient.client.event.impl.PreRenderChunkEvent;
import net.minecraft.client.renderer.ChunkRenderContainer;
import net.minecraft.client.renderer.chunk.RenderChunk;

@Mixin(ChunkRenderContainer.class)
public class MixinChunkRenderContainer {

	@Inject(method = "preRenderChunk", at = @At("RETURN"))
	public void preRenderChunk(RenderChunk renderChunkIn, CallbackInfo callback) {
		Client.INSTANCE.bus.post(new PreRenderChunkEvent(renderChunkIn));
	}

}
