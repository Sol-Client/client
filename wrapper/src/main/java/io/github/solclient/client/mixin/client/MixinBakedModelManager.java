package io.github.solclient.client.mixin.client;

import net.minecraft.client.render.block.BlockModelShapes;
import net.minecraft.client.render.model.*;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.resource.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

import io.github.solclient.client.util.Utils;

@Mixin(BakedModelManager.class)
public class MixinBakedModelManager {

	@Redirect(method = "reload", at = @At(value = "NEW", target = "net/minecraft/client/render/model/ModelLoader"))
	public ModelLoader captureModelBakery(ResourceManager resourceManager, SpriteAtlasTexture atlas, BlockModelShapes blockModelShapes) {
		return Utils.modelLoader = new ModelLoader(resourceManager, atlas, blockModelShapes);
	}

}
