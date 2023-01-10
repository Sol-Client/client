package io.github.solclient.client.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import io.github.solclient.client.util.extension.RenderExtension;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;

@Mixin(Render.class)
public abstract class MixinRender<T extends Entity> implements RenderExtension<T> {

	@Override
	@Invoker("renderName")
	public abstract void doRenderName(T entity, double x, double y, double z);

}
