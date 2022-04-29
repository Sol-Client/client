package me.mcblueparrot.client.event.impl;

import lombok.AllArgsConstructor;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.util.BlockPos;

@AllArgsConstructor
public class RenderChunkPositionEvent {

	public final RenderChunk chunk;
	public final BlockPos position;

}
