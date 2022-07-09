package io.github.solclient.client.event.impl.world.level;

import io.github.solclient.abstraction.mc.raycast.HitResult;
import io.github.solclient.client.event.Cancellable;
import io.github.solclient.client.event.impl.RenderEvent;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class BlockSelectionRenderEvent extends RenderEvent implements Cancellable {

	private HitResult hit;
	private boolean cancelled;

	public BlockSelectionRenderEvent(HitResult hit, float tickDelta) {
		super(tickDelta);
		this.hit = hit;
	}

}
