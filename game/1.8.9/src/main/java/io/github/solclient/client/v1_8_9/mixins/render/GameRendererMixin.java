package io.github.solclient.client.v1_8_9.mixins.render;

import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

import com.llamalad7.mixinextras.injector.WrapWithCondition;

import io.github.solclient.client.event.EventBus;
import io.github.solclient.client.event.impl.game.PreRenderEvent;
import io.github.solclient.client.event.impl.input.CameraRotateEvent;
import io.github.solclient.client.event.impl.world.*;
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

	private static float sc$yaw, sc$prevYaw, sc$pitch, sc$prevPitch;

	@Inject(method = "transformCamera", at = @At("HEAD"))
	public void rotationEvent(float partialTicks, CallbackInfo callback) {
		CameraTransformEvent event = EventBus.DEFAULT.post(new CameraTransformEvent(client.getCameraEntity().yaw, client.getCameraEntity().pitch));
		sc$yaw = event.getYaw();
		sc$pitch = event.getPitch();

		event = EventBus.DEFAULT.post(new CameraTransformEvent(client.getCameraEntity().prevYaw, client.getCameraEntity().prevPitch));
		sc$prevYaw = event.getYaw();
		sc$prevPitch = event.getPitch();
	}

	@Shadow
	private MinecraftClient client;

	@Redirect(method = "transformCamera", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/Entity;yaw:F"))
	public float eventYaw(Entity entity) {
		return sc$yaw;
	}

	@Redirect(method = "transformCamera", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/Entity;prevYaw:F"))
	public float eventPrevYaw(Entity entity) {
		return sc$prevYaw;
	}

	@Redirect(method = "transformCamera", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/Entity;pitch:F"))
	public float eventPitch(Entity entity) {
		return sc$pitch;
	}

	@Redirect(method = "transformCamera", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/Entity;prevPitch:F"))
	public float eventPrevPitch(Entity entity) {
		return sc$prevPitch;
	}

}
