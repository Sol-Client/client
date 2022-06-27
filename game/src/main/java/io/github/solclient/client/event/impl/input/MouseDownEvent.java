package io.github.solclient.client.event.impl.input;

import io.github.solclient.client.event.Cancellable;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class MouseDownEvent implements Cancellable {

	private final int button;
	private boolean cancelled;

}
