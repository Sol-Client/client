package io.github.solclient.client.event.impl.screen;

import io.github.solclient.client.platform.mc.screen.Screen;
import lombok.*;

@Data
@RequiredArgsConstructor
public final class ScreenSwitchEvent {

	private final Screen screen, previousScreen;

}
