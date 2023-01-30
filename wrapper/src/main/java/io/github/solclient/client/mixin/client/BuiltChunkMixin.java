package io.github.solclient.client.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.solclient.client.Client;
import io.github.solclient.client.event.impl.RenderChunkPositionEvent;
import net.minecraft.client.world.BuiltChunk;
import net.minecraft.util.math.BlockPos;

@Mixin(BuiltChunk.class)
public class BuiltChunkMixin {

	@Inject(method = "method_10160", at = @At("RETURN"))
	public void setPosition(BlockPos pos, CallbackInfo callback) {
		Client.INSTANCE.getEvents().post(new RenderChunkPositionEvent((BuiltChunk) (Object) this, pos));
	}

}
