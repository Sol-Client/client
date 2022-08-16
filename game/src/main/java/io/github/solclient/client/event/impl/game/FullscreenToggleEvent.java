package io.github.solclient.client.event.impl.game;

import io.github.solclient.client.event.Cancellable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public class FullscreenToggleEvent implements Cancellable {

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
