package io.github.solclient.client.event.impl.world;

import lombok.*;

@Data
@AllArgsConstructor
public final class CameraTransformEvent {

	private float yaw, pitch;

}
