package me.mcblueparrot.client.events;

import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.util.BlockPos;

public class RenderChunkPositionEvent {

    public RenderChunk chunk;
    public BlockPos position;

    public RenderChunkPositionEvent(RenderChunk chunk, BlockPos position) {
        this.chunk = chunk;
        this.position = position;
    }

}
