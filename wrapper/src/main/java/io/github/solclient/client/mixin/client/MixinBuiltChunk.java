package io.github.solclient.client.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.solclient.client.Client;
import io.github.solclient.client.event.impl.RenderChunkPositionEvent;
import io.github.solclient.util.GlobalConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexBuffer;
import net.minecraft.client.world.BuiltChunk;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;

@Mixin(BuiltChunk.class)
public class MixinBuiltChunk {

	/**
	 * @reason OptiFine for 1.8 has some issues on Linux. This fixed it, so I don't
	 *         question it.
	 *
	 *         TODO this is awful. I did this before patcher had a better fix.
	 */
	@Redirect(method = "delete", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/VertexBuffer;delete()V"))
	public void cancelDelete(VertexBuffer instance) {
		if (Util.getOperatingSystem() == Util.OperatingSystem.LINUX && MinecraftClient.getInstance().options.vbo
				&& GlobalConstants.OPTIFINE)
			return;

		instance.delete();
	}

	@Inject(method = "method_10160", at = @At("RETURN"))
	public void setPosition(BlockPos pos, CallbackInfo callback) {
		Client.INSTANCE.getEvents().post(new RenderChunkPositionEvent((BuiltChunk) (Object) this, pos));
	}

}
