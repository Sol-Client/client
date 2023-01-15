package io.github.solclient.client.mixin.client;

import net.minecraft.client.render.entity.ItemEntityRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.entity.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import io.github.solclient.client.Client;
import io.github.solclient.client.event.impl.ItemEntityRenderEvent;

@Mixin(ItemEntityRenderer.class)
public class MixinItemEntityRenderer {

	@Inject(method = "method_10221", at = @At(value = "HEAD"), cancellable = true)
	public void preItemEntityRender(ItemEntity itemEntity, double x, double y, double z, float tickDelta, BakedModel model, CallbackInfoReturnable<Integer> callback) {
		int result;
		if ((result = Client.INSTANCE.getEvents()
				.post(new ItemEntityRenderEvent(itemEntity, x, y, z, tickDelta, model)).result) != -1)
			callback.setReturnValue(result);
	}

}
