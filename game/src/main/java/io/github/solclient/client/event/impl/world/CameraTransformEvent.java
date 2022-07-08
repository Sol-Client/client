package io.github.solclient.client.event.impl.world;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class CameraTransformEvent {

	private float yaw, pitch;

}
