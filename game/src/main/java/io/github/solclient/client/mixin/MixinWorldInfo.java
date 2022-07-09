package io.github.solclient.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import io.github.solclient.client.Client;
import io.github.solclient.client.event.impl.TimeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.world.storage.WorldInfo;

@Mixin(WorldInfo.class)
public class MixinWorldInfo {

	private Minecraft mc = Minecraft.getMinecraft();

	@Inject(method = "getWorldTime", at = @At("HEAD"), cancellable = true)
	public void overrideWorldTime(CallbackInfoReturnable<Long> callback) {
		if(mc.theWorld != null && (Object) this == mc.theWorld.getWorldInfo()) {
			callback.setReturnValue(Client.INSTANCE.bus.post(new TimeEvent(worldTime)).time);
		}
	}

	@Shadow
	private long worldTime;

}
