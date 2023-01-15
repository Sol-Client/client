package io.github.solclient.client.util.extension;

import java.util.List;

import net.minecraft.client.gl.PostProcessShader;

public interface ShaderEffectExtension {

	List<PostProcessShader> getPasses();

}
