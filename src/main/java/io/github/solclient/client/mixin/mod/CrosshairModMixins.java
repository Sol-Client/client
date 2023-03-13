package io.github.solclient.client.mixin.mod;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.solclient.client.mod.impl.hud.crosshair.CrosshairMod;
import net.minecraft.client.render.GameRenderer;

public class CrosshairModMixins {

	@Mixin(GameRenderer.class)
	public static class GameRendererMixin {

		@Inject(method = "renderDebugCrosshair", at = @At("HEAD"), cancellable = true)
		public void hideGizmo(CallbackInfo callback) {
			if (CrosshairMod.enabled && CrosshairMod.instance.debug)
				callback.cancel();
		}

	}

}
