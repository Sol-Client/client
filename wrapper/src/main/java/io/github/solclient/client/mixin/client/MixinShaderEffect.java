package io.github.solclient.client.mixin.client;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import io.github.solclient.client.util.extension.ShaderEffectExtension;
import net.minecraft.client.gl.*;

@Mixin(ShaderEffect.class)
public abstract class MixinShaderEffect implements ShaderEffectExtension {

	@Override
	@Accessor
	public abstract List<PostProcessShader> getPasses();

}
