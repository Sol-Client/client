package io.github.solclient.client.v1_19_2.mixins.render;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

import io.github.solclient.client.event.EventBus;
import io.github.solclient.client.event.impl.game.PreRenderEvent;
import io.github.solclient.client.event.impl.world.FovEvent;
import io.github.solclient.client.v1_19_2.SharedObjects;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

	@Redirect(method = "render", at = @At(value = "NEW", target = "net/minecraft/client/util/math/MatrixStack", ordinal = 1))
	public MatrixStack sharedMatrixStack() {
		return SharedObjects.primary2dMatrixStack = new MatrixStack();
	}

	@Inject(method = "render", at = @At("HEAD"))
	public void preRender(CallbackInfo callback) {
		EventBus.DEFAULT.post(new PreRenderEvent());
	}

	@Inject(method = "getFov", at = @At("RETURN"), cancellable = true)
	public void fovEvent(Camera camera, float tickDelta, boolean changingFov, CallbackInfoReturnable<Double> callback) {
		callback.setReturnValue(
				EventBus.DEFAULT.post(new FovEvent(callback.getReturnValueD(), tickDelta)).getFov());
	}

}
