package io.github.solclient.client.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.client.renderer.block.model.ModelBlock;
import net.minecraft.client.resources.model.*;

@Mixin(ModelBakery.class)
public interface MixinModelBakery {

	@Invoker("bakeModel")
	public IBakedModel bakeBlockModel(ModelBlock modelBlockIn, ModelRotation modelRotationIn, boolean uvLocked);

}
