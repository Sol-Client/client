package io.github.solclient.client.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.solclient.client.Client;
import io.github.solclient.client.event.impl.CameraRotateEvent;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.player.PlayerEntity;

@Mixin(Camera.class)
public class CameraMixin {

	private static float sc$yaw, sc$pitch;

	@Inject(method = "update", at = @At("HEAD"))
	private static void rotationEvent(PlayerEntity player, boolean thirdPerson, CallbackInfo callback) {
		CameraRotateEvent event = Client.INSTANCE.getEvents().post(new CameraRotateEvent(player.yaw, player.pitch, 0));
		sc$yaw = event.yaw;
		sc$pitch = event.pitch;
	}

	@Redirect(method = "update", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/player/PlayerEntity;yaw:F"))
	private static float eventYaw(PlayerEntity ignored) {
		return sc$yaw;
	}

	@Redirect(method = "update", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/player/PlayerEntity;pitch:F"))
	private static float eventPitch(PlayerEntity ignored) {
		return sc$pitch;
	}

}
