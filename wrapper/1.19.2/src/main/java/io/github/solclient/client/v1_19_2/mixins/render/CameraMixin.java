package io.github.solclient.client.v1_19_2.mixins.render;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import io.github.solclient.client.event.EventBus;
import io.github.solclient.client.event.impl.world.CameraTransformEvent;
import net.minecraft.client.render.Camera;

@Mixin(Camera.class)
public class CameraMixin {

	@ModifyArgs(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Camera;setRotation(FF)V"))
	public void modifyRotation(Args args) {
		CameraTransformEvent event = EventBus.DEFAULT.post(new CameraTransformEvent((float) args.get(0), (float) args.get(1)));
		args.set(0, event.getYaw());
		args.set(1, event.getPitch());
	}

}
