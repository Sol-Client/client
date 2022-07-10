package io.github.solclient.client.platform.mc.shader;

import org.jetbrains.annotations.Nullable;

import io.github.solclient.client.platform.Helper;

public interface Shader {

	@Helper
	@Nullable ShaderUniform getUniform(String name);

}
