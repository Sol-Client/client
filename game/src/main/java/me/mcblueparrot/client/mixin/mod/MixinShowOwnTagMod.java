package me.mcblueparrot.client.mixin.mod;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import me.mcblueparrot.client.mod.impl.ShowOwnTagMod;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.Entity;

public class MixinShowOwnTagMod {

	@Mixin(RendererLivingEntity.class)
	public static class MixinRendererLivingEntity {

		@Redirect(method = "canRenderName", at = @At(value = "FIELD",
				target = "Lnet/minecraft/client/renderer/entity/RenderManager;livingPlayer:Lnet/minecraft/entity/Entity;"))
		public Entity renderOwnName(RenderManager manager) {
			if(ShowOwnTagMod.enabled) {
				return null;
			}
			return manager.livingPlayer;
		}

	}

}
