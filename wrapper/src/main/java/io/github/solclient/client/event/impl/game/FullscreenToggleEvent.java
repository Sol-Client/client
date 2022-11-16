package io.github.solclient.client.event.impl.game;

import io.github.solclient.client.event.Cancellable;
import lombok.*;

@RequiredArgsConstructor
public final class FullscreenToggleEvent implements Cancellable {

	@Setter
	private boolean state, applyState = true;
	@Getter
	@Setter
	private boolean cancelled;

	public boolean getState() {
		return state;
	}

	public boolean getApplyState() {
		return applyState;
	}

}
