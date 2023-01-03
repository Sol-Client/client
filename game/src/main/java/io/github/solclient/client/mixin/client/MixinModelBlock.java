package io.github.solclient.client.mixin.client;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.renderer.block.model.ModelBlock;

@Mixin(ModelBlock.class)
public interface MixinModelBlock {

	@Accessor
	public Map<String, String> getTextures();

}
