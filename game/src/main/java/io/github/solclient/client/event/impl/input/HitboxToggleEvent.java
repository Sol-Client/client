package io.github.solclient.client.event.impl.input;

import lombok.*;

@RequiredArgsConstructor
public class HitboxToggleEvent {

	@Setter
	private boolean state;

	public boolean getState() {
		return state;
	}

}
