package io.github.solclient.client.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.solclient.client.Client;
import io.github.solclient.client.event.impl.RenderChunkPositionEvent;
import io.github.solclient.client.tweak.Tweaker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.util.*;
import net.minecraft.util.Util.EnumOS;

@Mixin(RenderChunk.class)
public class MixinRenderChunk {

	/**
	 * @reason OptiFine for 1.8 has some issues on Linux. This fixed it, so I don't
	 *         question it.
	 */
	@Redirect(method = "deleteGlResources", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/vertex/"
			+ "VertexBuffer;deleteGlBuffers()V"))
	public void cancelDelete(VertexBuffer instance) {
		if (Util.getOSType() == EnumOS.LINUX && Minecraft.getMinecraft().gameSettings.useVbo && Tweaker.optiFine) {
			return;
		}

		instance.deleteGlBuffers();
	}

	@Inject(method = "setPosition", at = @At("RETURN"))
	public void setPosition(BlockPos pos, CallbackInfo callback) {
		Client.INSTANCE.getEvents().post(new RenderChunkPositionEvent((RenderChunk) (Object) this, pos));
	}

}
