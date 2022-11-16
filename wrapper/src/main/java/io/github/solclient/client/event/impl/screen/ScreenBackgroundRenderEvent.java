package io.github.solclient.client.event.impl.screen;

import io.github.solclient.client.event.Cancellable;
import io.github.solclient.client.platform.mc.screen.Screen;
import lombok.*;

@Data
@RequiredArgsConstructor
public final class ScreenBackgroundRenderEvent implements Cancellable {

	private final Screen screen;
	private boolean cancelled;

}
