package me.mcblueparrot.client.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.mcblueparrot.client.Client;
import me.mcblueparrot.client.event.impl.RenderChunkPositionEvent;
import me.mcblueparrot.client.tweak.Tweaker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Util;
import net.minecraft.util.Util.EnumOS;

@Mixin(RenderChunk.class)
public class MixinRenderChunk {

	@Redirect(method = "deleteGlResources", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/vertex/" +
			"VertexBuffer;deleteGlBuffers()V"))
	public void cancelDelete(VertexBuffer instance) {
		if(Util.getOSType() == EnumOS.LINUX
				&& Minecraft.getMinecraft().gameSettings.useVbo
				&& Tweaker.optiFine) {
			return;
		}

		instance.deleteGlBuffers();
	}

	@Inject(method = "setPosition", at = @At("RETURN"))
	public void setPosition(BlockPos pos, CallbackInfo callback) {
		Client.INSTANCE.bus.post(new RenderChunkPositionEvent((RenderChunk) (Object) this, pos));
	}

}
