package io.github.solclient.client.event.impl.world.entity.render;

import io.github.solclient.abstraction.mc.world.entity.Entity;
import io.github.solclient.client.event.Cancellable;
import io.github.solclient.client.event.impl.RenderEvent;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class HitboxRenderEvent extends RenderEvent implements Cancellable {

	private final Entity entity;
	private final double x, y, z;
	private boolean cancelled;

	public HitboxRenderEvent(Entity entity, float tickDelta, double x, double y, double z) {
		super(tickDelta);
		this.entity = entity;
		this.x = x;
		this.y = y;
		this.z = z;
	}

}
