package io.github.solclient.client.event.impl.world.level;

import io.github.solclient.client.platform.mc.world.level.ClientLevel;
import lombok.*;

@Data
@RequiredArgsConstructor
public final class LevelLoadEvent {

	private final ClientLevel level;

}
