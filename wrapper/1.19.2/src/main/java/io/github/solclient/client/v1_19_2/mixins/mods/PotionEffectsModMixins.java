package io.github.solclient.client.v1_19_2.mixins.mods;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.solclient.client.mod.impl.hud.PotionEffectsMod;
import net.minecraft.client.gui.hud.InGameHud;

public class PotionEffectsModMixins {

}

// hides existing potion effects
@Mixin(InGameHud.class)
class PotionEffectsModMixins$InGameHudMixin {

	@Inject(method = "renderStatusEffectOverlay", at = @At("HEAD"), cancellable = true)
	public void cancelRendering(CallbackInfo callback) {
		if(PotionEffectsMod.INSTANCE.isEnabled()) {
			callback.cancel();
		}
	}

}