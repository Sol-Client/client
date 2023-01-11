package io.github.solclient.client.mixin.client;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import io.github.solclient.client.util.extension.ShaderGroupExtension;
import net.minecraft.client.shader.*;

@Mixin(ShaderGroup.class)
public abstract class MixinShaderGroup implements ShaderGroupExtension {

	@Override
	@Accessor
	public abstract List<Shader> getListShaders();

}
