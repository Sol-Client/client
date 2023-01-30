package io.github.solclient.client.mixin.client;

import java.nio.ByteBuffer;

import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.render.VertexBuffer;

@Mixin(VertexBuffer.class)
public class VertexBufferMixin {

	// Thanks Sychic!
	// this is a much better way of doing what we were doing before
	// https://github.com/Sk1erLLC/Patcher/pull/98

	@Inject(method = "data", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/VertexBuffer;bind()V"), cancellable = true)
	public void preventBindingToDeleted(ByteBuffer buffer, CallbackInfo callback) {
		if (id == -1)
			callback.cancel();
	}

	@Shadow
	private int id;

}
