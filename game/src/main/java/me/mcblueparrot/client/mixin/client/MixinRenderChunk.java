package me.mcblueparrot.client.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.mcblueparrot.client.Client;
import me.mcblueparrot.client.event.impl.RenderChunkPositionEvent;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.util.BlockPos;

@Mixin(RenderChunk.class)
public class MixinRenderChunk {

	/**
	 * @reason OptiFine for 1.8 has some issues on Linux. This fixed it, so I don't question it.
	 */
	@Redirect(method = "deleteGlResources", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/vertex/" +
			"VertexBuffer;deleteGlBuffers()V"))
	public void cancelDelete(VertexBuffer instance) {
	}

	@Inject(method = "setPosition", at = @At("RETURN"))
	public void setPosition(BlockPos pos, CallbackInfo callback) {
		Client.INSTANCE.bus.post(new RenderChunkPositionEvent((RenderChunk) (Object) this, pos));
	}

}
