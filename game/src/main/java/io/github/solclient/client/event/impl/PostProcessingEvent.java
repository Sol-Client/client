package io.github.solclient.client.event.impl;

import java.util.LinkedList;
import java.util.List;

import lombok.RequiredArgsConstructor;
import net.minecraft.client.shader.ShaderGroup;

@RequiredArgsConstructor
public class PostProcessingEvent {

	public final Type type;
	public final List<ShaderGroup> groups = new LinkedList<>();

	public enum Type {
		RENDER,
		UPDATE
	}

}
