package io.github.solclient.client.mod.impl.hud.crosshair.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.solclient.client.mod.impl.hud.crosshair.CrosshairMod;
import net.minecraft.client.render.GameRenderer;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

	@Inject(method = "renderDebugCrosshair", at = @At("HEAD"), cancellable = true)
	public void hideGizmo(CallbackInfo callback) {
		if (CrosshairMod.enabled && CrosshairMod.instance.debug)
			callback.cancel();
	}

}