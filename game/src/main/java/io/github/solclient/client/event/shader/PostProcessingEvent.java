package io.github.solclient.client.event.shader;

import java.util.List;

import io.github.solclient.abstraction.mc.shader.ShaderChain;
import lombok.Data;

@Data
public class PostProcessingEvent {

	private List<ShaderChain> shaders;

}
