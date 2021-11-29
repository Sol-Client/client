package me.mcblueparrot.client.event.impl;

import lombok.AllArgsConstructor;
import net.minecraft.client.renderer.chunk.RenderChunk;

@AllArgsConstructor
public class PreRenderChunkEvent {

	public RenderChunk chunk;

}
