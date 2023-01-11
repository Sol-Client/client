package io.github.solclient.client.event.impl;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CameraRotateEvent {

	public float yaw;
	public float pitch;
	public float roll;

}
