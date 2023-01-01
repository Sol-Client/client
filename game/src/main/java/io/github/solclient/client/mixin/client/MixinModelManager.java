package io.github.solclient.client.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

import io.github.solclient.client.util.Utils;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.model.*;

@Mixin(ModelManager.class)
public class MixinModelManager {

	@Redirect(method = "onResourceManagerReload", at = @At(value = "NEW", target = "net/minecraft/client/resources/model/ModelBakery"))
	public ModelBakery captureModelBakery(IResourceManager resourceManager, TextureMap textures,
			BlockModelShapes shapes) {
		return Utils.modelBakery = new ModelBakery(resourceManager, textures, shapes);
	}

}
