package io.github.solclient.client.event.impl.world;

import lombok.*;

@Data
@AllArgsConstructor
public class CameraTransformEvent {

	private float yaw, pitch;

}
