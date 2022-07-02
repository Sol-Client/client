package io.github.solclient.client.event.impl.world.level;

import io.github.solclient.abstraction.mc.raycast.HitResult;
import io.github.solclient.client.event.Cancellable;
import io.github.solclient.client.event.impl.RenderEvent;
import lombok.Getter;
import lombok.Setter;

public class BlockSelectionRenderEvent extends RenderEvent implements Cancellable {

	@Getter
	@Setter
	private boolean cancelled;
	@Getter
	private HitResult hit;

	public BlockSelectionRenderEvent(float tickDelta) {
		super(tickDelta);
	}

}
