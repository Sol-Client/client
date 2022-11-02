package io.github.solclient.client.event.impl.input;

import io.github.solclient.client.event.Cancellable;
import lombok.*;

@Data
@RequiredArgsConstructor
public final class ScrollWheelEvent implements Cancellable {

	private final int amount;
	private boolean cancelled;

}
