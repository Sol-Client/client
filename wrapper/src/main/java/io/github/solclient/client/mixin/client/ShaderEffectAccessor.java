package io.github.solclient.client.mixin.client;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.gl.*;

@Mixin(ShaderEffect.class)
public interface ShaderEffectAccessor {

	@Accessor
	public List<PostProcessShader> getPasses();

}
