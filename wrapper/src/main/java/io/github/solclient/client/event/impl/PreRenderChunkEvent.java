package io.github.solclient.client.event.impl;

import lombok.AllArgsConstructor;
import net.minecraft.client.renderer.chunk.RenderChunk;

@AllArgsConstructor
public class PreRenderChunkEvent {

	public final RenderChunk chunk;

}
