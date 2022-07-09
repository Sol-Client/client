package io.github.solclient.client.event.impl.input;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public class HitboxToggleEvent {

	@Setter
	private boolean state;

	public boolean getState() {
		return state;
	}

}
