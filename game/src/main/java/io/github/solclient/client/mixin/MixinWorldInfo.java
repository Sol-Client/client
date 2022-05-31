package io.github.solclient.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import io.github.solclient.client.Client;
import io.github.solclient.client.event.impl.TimeEvent;
import net.minecraft.world.storage.WorldInfo;

@Mixin(WorldInfo.class)
public class MixinWorldInfo {

	@Inject(method = "getWorldTime", at = @At("HEAD"), cancellable = true)
	public void overrideWorldTime(CallbackInfoReturnable<Long> callback) {
		callback.setReturnValue(Client.INSTANCE.bus.post(new TimeEvent(worldTime)).time);
	}

	@Shadow
	private long worldTime;

}
