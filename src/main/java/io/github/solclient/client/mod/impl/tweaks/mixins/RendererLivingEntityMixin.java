package io.github.solclient.client.mod.impl.tweaks.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

import io.github.solclient.client.mod.impl.tweaks.TweaksMod;
import net.minecraft.client.render.entity.*;
import net.minecraft.entity.Entity;

@Mixin(LivingEntityRenderer.class)
public class RendererLivingEntityMixin {

	@Redirect(method = "hasLabel(Lnet/minecraft/entity/LivingEntity;)Z", at = @At(value = "FIELD", target = "Lnet/minecraft/client/render/entity/EntityRenderDispatcher;field_11098:Lnet/minecraft/entity/Entity;"))
	public Entity renderOwnName(EntityRenderDispatcher dispatcher) {
		if (TweaksMod.enabled && TweaksMod.instance.showOwnTag)
			return null;

		return dispatcher.field_11098;
	}

}