package io.github.solclient.client.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.solclient.client.Client;
import io.github.solclient.client.event.impl.PreRenderChunkEvent;
import net.minecraft.client.render.world.AbstractChunkRenderManager;
import net.minecraft.client.world.BuiltChunk;

@Mixin(AbstractChunkRenderManager.class)
public class AbstractChunkRenderManagerMixin {

	@Inject(method = "method_9770", at = @At("RETURN"))
	public void preRenderChunk(BuiltChunk helper, CallbackInfo ci) {
		Client.INSTANCE.getEvents().post(new PreRenderChunkEvent(helper));
	}

}
