package io.github.solclient.client.v1_8_9.mixins.render.entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.solclient.client.event.EventBus;
import io.github.solclient.client.event.impl.world.CameraTransformEvent;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin {

	private static final String UPDATE_CAMERA = "updateCamera(Lnet/minecraft/world/World;Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/entity/Entity;Lnet/minecraft/entity/Entity;Lnet/minecraft/client/options/GameOptions;F)V";
	private static float sc$yaw, sc$prevYaw, sc$pitch, sc$prevPitch;

	@Inject(method = UPDATE_CAMERA, at = @At("HEAD"))
	public void rotationEvent(World world, TextRenderer textRenderer, Entity entity, Entity entity2, GameOptions gameOptions,
			float tickDelta, CallbackInfo callback) {
		CameraTransformEvent event = EventBus.DEFAULT.post(new CameraTransformEvent(entity.yaw, entity.pitch));
		sc$yaw = event.getYaw();
		sc$pitch = event.getPitch();

		event = EventBus.DEFAULT.post(new CameraTransformEvent(entity.prevYaw, entity.prevPitch));
		sc$prevYaw = event.getYaw();
		sc$prevPitch = event.getPitch();
	}

	@Redirect(method = UPDATE_CAMERA, at = @At(value = "FIELD", target = "Lnet/minecraft/entity/Entity;yaw:F"))
	public float eventYaw(Entity ignored) {
		return sc$yaw;
	}

	@Redirect(method = UPDATE_CAMERA, at = @At(value = "FIELD", target = "Lnet/minecraft/entity/Entity;prevYaw:F"))
	public float eventPrevYaw(Entity ignored) {
		return sc$prevYaw;
	}

	@Redirect(method = UPDATE_CAMERA, at = @At(value = "FIELD", target = "Lnet/minecraft/entity/Entity;pitch:F"))
	public float eventPitch(Entity ignored) {
		return sc$pitch;
	}

	@Redirect(method = UPDATE_CAMERA, at = @At(value = "FIELD", target = "Lnet/minecraft/entity/Entity;prevPitch:F"))
	public float eventPrevPitch(Entity ignored) {
		return sc$prevPitch;
	}

}
