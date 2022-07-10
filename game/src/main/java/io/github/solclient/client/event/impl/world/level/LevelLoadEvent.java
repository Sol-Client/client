package io.github.solclient.client.event.impl.world.level;

import io.github.solclient.client.platform.mc.world.level.ClientLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class LevelLoadEvent {

	private final ClientLevel level;

}
