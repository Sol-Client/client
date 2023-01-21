package io.github.solclient.client.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;

@Mixin(ItemRenderer.class)
public interface MixinItemRenderer {

	@Invoker("renderBakedItemModel")
	void renderBakedModel(BakedModel model, int colour);

}
