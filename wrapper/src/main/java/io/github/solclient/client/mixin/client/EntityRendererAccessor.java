package io.github.solclient.client.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;

@Mixin(EntityRenderer.class)
public interface EntityRendererAccessor<T extends Entity> {

	@Invoker("method_10208")
	void renderName(T entity, double x, double y, double z);

}
