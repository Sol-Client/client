package io.github.solclient.client.event.impl.world.level.chunk;

import io.github.solclient.client.platform.mc.world.level.chunk.CompiledChunk;
import lombok.*;

@Data
@RequiredArgsConstructor
public class PreRenderChunkEvent {

	private final CompiledChunk chunk;

}
