package io.github.solclient.client.event.impl;

import io.github.solclient.client.event.Cancellable;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class RenderEvent {

	private final float tickDelta;

}
