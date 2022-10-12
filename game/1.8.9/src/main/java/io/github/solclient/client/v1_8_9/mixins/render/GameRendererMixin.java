package io.github.solclient.client.v1_8_9.mixins.render;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.injector.WrapWithCondition;

import io.github.solclient.client.event.EventBus;
import io.github.solclient.client.event.impl.game.PreRenderEvent;
import io.github.solclient.client.event.impl.input.CameraRotateEvent;
import io.github.solclient.client.event.impl.world.CameraTransformEvent;
import io.github.solclient.client.event.impl.world.FovEvent;
import io.github.solclient.client.event.impl.world.GammaEvent;
import io.github.solclient.client.util.Utils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ClientPlayerEntity;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

	@Inject(method = "render", at = @At("HEAD"))
	public void preRender(CallbackInfo callback) {
		EventBus.DEFAULT.post(new PreRenderEvent());
	}

	@Inject(method = "getFov", at = @At("RETURN"), cancellable = true)
	public void fovEvent(float tickDelta, boolean changingFov, CallbackInfoReturnable<Float> callback) {
		callback.setReturnValue(
				(float) EventBus.DEFAULT.post(new FovEvent(callback.getReturnValueF(), tickDelta)).getFov());
	}

	@Redirect(method = "updateLightmap", at = @At(value = "FIELD", target = "Lnet/minecraft/client/options/GameOptions;gamma:F"))
	public float modifyGamma(GameOptions options) {
		return EventBus.DEFAULT.post(new GammaEvent(options.gamma)).getGamma();
	}

	@WrapWithCondition(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/ClientPlayerEntity;increaseTransforms(FF)V"))
	public boolean cameraRotateEvent(ClientPlayerEntity entity, float yaw, float pitch) {
		CameraRotateEvent event = EventBus.DEFAULT.post(new CameraRotateEvent(yaw, pitch));
		yaw = event.getYaw();
		pitch = event.getPitch();
		return !event.isCancelled() && !Utils.isSpectatingEntityInReplay();
	}

	private static float rotationYaw;
	private static float prevRotationYaw;
	private static float rotationPitch;
	private static float prevRotationPitch;

	@Inject(method = "transformCamera", at = @At("HEAD"))
	public void rotationEvent(float partialTicks, CallbackInfo callback) {
		rotationYaw = client.getCameraEntity().yaw;
		prevRotationYaw = client.getCameraEntity().prevYaw;
		rotationPitch = client.getCameraEntity().pitch;
		prevRotationPitch = client.getCameraEntity().prevPitch;

		CameraTransformEvent event = EventBus.DEFAULT.post(new CameraTransformEvent(rotationYaw, rotationPitch));
		rotationYaw = event.getYaw();
		rotationPitch = event.getPitch();

		event = EventBus.DEFAULT.post(new CameraTransformEvent(prevRotationYaw, prevRotationPitch));
		prevRotationYaw = event.getYaw();
		prevRotationPitch = event.getPitch();
	}

	@Shadow
	private MinecraftClient client;

	@Redirect(method = "transformCamera", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/Entity;yaw:F"))
	public float eventYaw(Entity entity) {
		return rotationYaw;
	}

	@Redirect(method = "transformCamera", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/Entity;prevYaw:F"))
	public float eventPrevYaw(Entity entity) {
		return prevRotationYaw;
	}

	@Redirect(method = "transformCamera", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/Entity;pitch:F"))
	public float eventPitch(Entity entity) {
		return rotationPitch;
	}

	@Redirect(method = "transformCamera", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/Entity;prevPitch:F"))
	public float eventPrevPitch(Entity entity) {
		return prevRotationPitch;
	}

}
