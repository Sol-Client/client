package io.github.solclient.client.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import io.github.solclient.client.extension.EntityRendererExtension;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;

@Mixin(EntityRenderer.class)
public abstract class MixinEntityRenderer<T extends Entity> implements EntityRendererExtension<T> {

	@Override
	@Invoker("method_10208")
	public abstract void renderName(T entity, double x, double y, double z);

}
