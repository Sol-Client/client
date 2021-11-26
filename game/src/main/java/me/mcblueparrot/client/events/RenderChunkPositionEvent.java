package me.mcblueparrot.client.events;

import lombok.AllArgsConstructor;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.util.BlockPos;

@AllArgsConstructor
public class RenderChunkPositionEvent {

	public RenderChunk chunk;
	public BlockPos position;

}
