package io.github.solclient.client.event.impl.screen;

import io.github.solclient.abstraction.mc.screen.Screen;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class ScreenSwitchEvent {

	private final Screen screen;

}
