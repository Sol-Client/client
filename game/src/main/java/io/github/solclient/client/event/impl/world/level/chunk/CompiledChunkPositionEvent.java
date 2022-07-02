package io.github.solclient.client.event.impl.world.level.chunk;

import io.github.solclient.abstraction.mc.world.level.chunk.CompiledChunk;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class CompiledChunkPositionEvent {

	private final CompiledChunk chunk;

}
