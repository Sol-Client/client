package io.github.solclient.client.event.impl.world;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CameraTransformEvent {

	private float yaw, pitch;

}
