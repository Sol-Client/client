package me.mcblueparrot.client.events;

import net.minecraft.client.renderer.chunk.RenderChunk;

public class PreRenderChunkEvent {

    public RenderChunk chunk;

    public PreRenderChunkEvent(RenderChunk chunk) {
        this.chunk = chunk;
    }

}
