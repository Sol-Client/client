package io.github.solclient.client.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import io.github.solclient.client.Client;
import io.github.solclient.client.event.impl.ItemEntityRenderEvent;
import net.minecraft.client.renderer.entity.RenderEntityItem;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.entity.item.EntityItem;

@Mixin(RenderEntityItem.class)
public class MixinRenderEntityItem {

	@Inject(method = "func_177077_a", at = @At(value = "HEAD"), cancellable = true)
	public void preItemEntityRender(EntityItem itemIn, double x, double y, double z, float partialTicks,
			IBakedModel model, CallbackInfoReturnable<Integer> callback) {
		int result;
		if ((result = Client.INSTANCE.getEvents()
				.post(new ItemEntityRenderEvent(itemIn, x, y, z, partialTicks, model)).result) != -1) {
			callback.setReturnValue(result);
		}
	}

}
