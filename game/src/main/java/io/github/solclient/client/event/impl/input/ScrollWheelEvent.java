package io.github.solclient.client.event.impl.input;

import io.github.solclient.client.event.Cancellable;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class ScrollWheelEvent implements Cancellable {

	private final int amount;
	private boolean cancelled;

}
