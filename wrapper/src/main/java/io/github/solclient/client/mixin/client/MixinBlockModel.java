package io.github.solclient.client.mixin.client;

import java.util.Map;

import net.minecraft.client.render.model.json.BlockModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BlockModel.class)
public interface MixinBlockModel {

	@Accessor
	Map<String, String> getTextures();

}
