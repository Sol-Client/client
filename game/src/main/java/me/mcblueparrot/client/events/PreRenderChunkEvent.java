package me.mcblueparrot.client.events;

import lombok.AllArgsConstructor;
import net.minecraft.client.renderer.chunk.RenderChunk;

@AllArgsConstructor
public class PreRenderChunkEvent {

	public RenderChunk chunk;

}
