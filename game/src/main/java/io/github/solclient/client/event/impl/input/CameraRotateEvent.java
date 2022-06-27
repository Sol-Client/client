package io.github.solclient.client.event.impl.input;

import io.github.solclient.client.event.Cancellable;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class CameraRotateEvent implements Cancellable {

	private final float yaw;
	private final float pitch;
	private boolean cancelled;

}
