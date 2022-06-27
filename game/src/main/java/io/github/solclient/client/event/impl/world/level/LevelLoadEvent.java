package io.github.solclient.client.event.impl.world.level;

import io.github.solclient.abstraction.mc.world.level.ClientLevel;
import io.github.solclient.abstraction.mc.world.level.Level;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class LevelLoadEvent {

	private final ClientLevel level;

}
