package io.github.solclient.client.mixin.client;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.render.model.json.BlockModel;

@Mixin(BlockModel.class)
public interface MixinBlockModel {

	@Accessor("textureMap")
	Map<String, String> getTextures();

}
