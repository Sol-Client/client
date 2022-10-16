package io.github.solclient.client.v1_8_9.mixins.render;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.solclient.client.event.EventBus;
import io.github.solclient.client.event.impl.world.CameraTransformEvent;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.player.PlayerEntity;

@Mixin(Camera.class)
public class CameraMixin {

	private static float sc$yaw, sc$pitch;

	@Inject(method = "update", at = @At("HEAD"))
	private static void rotationEvent(PlayerEntity player, boolean thirdPerson, CallbackInfo callback) {
		CameraTransformEvent event = EventBus.DEFAULT.post(new CameraTransformEvent(player.yaw, player.pitch));
		sc$yaw = event.getYaw();
		sc$pitch = event.getPitch();
	}

	@Redirect(method = "update", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/player/PlayerEntity;yaw"))
	private static float eventYaw(PlayerEntity ignored) {
		return sc$yaw;
	}

	@Redirect(method = "update", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/player/PlayerEntity;pitch"))
	private static float eventPitch(PlayerEntity ignored) {
		return sc$pitch;
	}

}
