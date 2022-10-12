package io.github.solclient.client.event.impl.input;

import io.github.solclient.client.event.Cancellable;
import lombok.*;

@Data
@RequiredArgsConstructor
public class CameraRotateEvent implements Cancellable {

	private final float yaw, pitch;
	private boolean cancelled;

}
