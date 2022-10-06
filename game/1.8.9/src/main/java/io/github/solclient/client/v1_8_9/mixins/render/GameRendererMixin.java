package io.github.solclient.client.v1_8_9.mixins.render;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import io.github.solclient.client.event.EventBus;
import io.github.solclient.client.event.impl.game.PreRenderEvent;
import io.github.solclient.client.event.impl.world.FovEvent;
import io.github.solclient.client.event.impl.world.GammaEvent;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.render.GameRenderer;

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

}
