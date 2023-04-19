package io.github.solclient.client.mod.impl.v1_7visuals.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import io.github.solclient.client.mod.impl.v1_7visuals.V1_7VisualsMod;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;

@Mixin(ArmorFeatureRenderer.class)
public class LayerArmorBaseMixin {

	@Inject(method = "combineTextures", at = @At("HEAD"), cancellable = true)
	public void oldArmour(CallbackInfoReturnable<Boolean> callback) {
		if (V1_7VisualsMod.enabled && V1_7VisualsMod.instance.armourDamage)
			callback.setReturnValue(true);
	}

}
