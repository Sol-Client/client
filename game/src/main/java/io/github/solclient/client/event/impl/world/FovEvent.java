package io.github.solclient.client.event.impl.world;

import io.github.solclient.client.event.impl.RenderEvent;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class FovEvent extends RenderEvent {

	private float fov;

	public FovEvent(int fov, float tickDelta) {
		super(tickDelta);
		this.fov = fov;
	}

}
