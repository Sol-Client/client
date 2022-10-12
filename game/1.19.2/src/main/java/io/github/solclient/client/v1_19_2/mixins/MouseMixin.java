package io.github.solclient.client.v1_19_2.mixins;

import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.WrapWithCondition;

import io.github.solclient.client.event.EventBus;
import io.github.solclient.client.event.impl.input.*;
import net.minecraft.client.*;
import net.minecraft.client.network.ClientPlayerEntity;

@Mixin(Mouse.class)
public class MouseMixin {

	@Inject(method = "onMouseButton", at = @At("HEAD"), cancellable = true)
	public void preMousePressed(long window, int button, int action, int mods, CallbackInfo callback) {
		// it's probably the correct window
		// let's hope that it is
		// otherwise Bad Things will happen
		if(action == 1 && client.currentScreen == null) {
			if(EventBus.DEFAULT.post(new MouseDownEvent(button)).isCancelled()) {
				callback.cancel();
			}
		}
	}

	@Shadow
	private @Final MinecraftClient client;

	@Inject(method = "onMouseScroll", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Mouse;eventDeltaWheel:D", ordinal = 7), cancellable = true)
	public void preMouseScroll(CallbackInfo callback) {
		if(EventBus.DEFAULT.post(new ScrollWheelEvent((int) eventDeltaWheel)).isCancelled()) {
			callback.cancel();
		}
	}

	@Shadow
	private double eventDeltaWheel;

	@WrapWithCondition(method = "updateMouse", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;changeLookDirection(DD)V"), require = 1)
	public boolean cancelMouseMovement(ClientPlayerEntity instance, double x, double y) {
		return !EventBus.DEFAULT.post(new CameraRotateEvent((float) x, (float) -y)).isCancelled();
	}

}
