package io.github.solclient.client.event.impl;

import lombok.AllArgsConstructor;
import net.minecraft.client.world.BuiltChunk;

@AllArgsConstructor
public class PreRenderChunkEvent {

	public final BuiltChunk chunk;

}
