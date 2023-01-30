package io.github.solclient.client.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.client.render.model.*;
import net.minecraft.client.render.model.json.BlockModel;

@Mixin(ModelLoader.class)
public interface ModelLoaderAccessor {

	@Invoker("method_10386")
	BakedModel bakeBlockModel(BlockModel model, ModelRotation rotation, boolean uvLocked);

}
