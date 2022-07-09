package io.github.solclient.client.event.impl.world.entity.render;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class HitOverlayRenderEvent {

	private float r, g, b, a;

}
