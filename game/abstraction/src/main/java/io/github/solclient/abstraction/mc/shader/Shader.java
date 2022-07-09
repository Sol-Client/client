package io.github.solclient.abstraction.mc.shader;

import org.jetbrains.annotations.Nullable;

import io.github.solclient.abstraction.Helper;

public interface Shader {

	@Helper
	@Nullable ShaderUniform getShaderUniform(String name);

}
