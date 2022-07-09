package io.github.solclient.client.event.impl.screen;

import io.github.solclient.abstraction.mc.screen.Screen;
import io.github.solclient.client.event.Cancellable;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class ScreenBackgroundRenderEvent implements Cancellable {

	private final Screen screen;
	private boolean cancelled;

}
