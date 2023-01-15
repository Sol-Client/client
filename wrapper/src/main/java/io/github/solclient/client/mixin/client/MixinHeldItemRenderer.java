package io.github.solclient.client.mixin.client;

import net.minecraft.client.render.item.HeldItemRenderer;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.solclient.client.Client;
import io.github.solclient.client.event.impl.TransformFirstPersonItemEvent;
import net.minecraft.item.ItemStack;

@Mixin(HeldItemRenderer.class)
public class MixinHeldItemRenderer {

	@Shadow
	private ItemStack mainHand;

	@Inject(method = "applyEquipAndSwingOffset", at = @At("HEAD"))
	public void applyEquipAndSwingOffset(float equipProgress, float swingProgress, CallbackInfo callback) {
		Client.INSTANCE.getEvents()
				.post(new TransformFirstPersonItemEvent(mainHand, equipProgress, swingProgress));
	}

}
