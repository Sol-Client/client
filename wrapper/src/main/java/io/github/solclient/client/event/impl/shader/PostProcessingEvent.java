package io.github.solclient.client.event.impl.shader;

import java.util.List;

import io.github.solclient.client.platform.mc.shader.ShaderChain;
import lombok.Data;

@Data
public final class PostProcessingEvent {

	private final Type type;
	private final List<ShaderChain> shaders;

	public enum Type {
		RENDER,
		UPDATE
	}

}
