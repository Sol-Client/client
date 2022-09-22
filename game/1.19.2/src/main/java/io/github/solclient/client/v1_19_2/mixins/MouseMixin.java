package io.github.solclient.client.v1_19_2.mixins;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.solclient.client.Client;
import io.github.solclient.client.event.impl.input.MouseDownEvent;
import io.github.solclient.client.event.impl.input.ScrollWheelEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;

@Mixin(Mouse.class)
public class MouseMixin {

	@Inject(method = "onMouseButton", at = @At("HEAD"), cancellable = true)
	public void preMousePressed(long window, int button, int action, int mods, CallbackInfo callback) {
		// it's probably the correct window
		// let's hope that it is
		// otherwise Bad Things will happen
		if(action == 1 && client.currentScreen == null) {
			if(Client.INSTANCE.getBus().post(new MouseDownEvent(button)).isCancelled()) {
				callback.cancel();
			}
		}
	}

	@Shadow
	private @Final MinecraftClient client;

	@Inject(method = "onMouseScroll", at = @At(value = "INVOKE", target = "eventDeltaWheel:Lnet/minecraft/client/Mouse;"))
	public void preMouseScroll(long window, double x /* I've never seen a mouse do this before */,
			double y/* , double z, double w? when libraries try to be too future-proof */, CallbackInfo callback, int amount /* applause */) {
		Client.INSTANCE.getBus().post(new ScrollWheelEvent(amount));
	}

}
