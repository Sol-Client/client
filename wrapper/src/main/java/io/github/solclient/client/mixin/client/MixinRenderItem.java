package io.github.solclient.client.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.resources.model.IBakedModel;

@Mixin(RenderItem.class)
public interface MixinRenderItem {

	@Invoker("renderModel")
	void renderBakedModel(BakedModel model, int colour);

}
