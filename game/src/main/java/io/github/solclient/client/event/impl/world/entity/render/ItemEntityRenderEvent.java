package io.github.solclient.client.event.impl.world.entity.render;

import io.github.solclient.abstraction.mc.model.BakedModel;
import io.github.solclient.abstraction.mc.world.entity.item.ItemEntity;
import io.github.solclient.client.event.Cancellable;
import io.github.solclient.client.event.impl.RenderEvent;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ItemEntityRenderEvent extends RenderEvent implements Cancellable {

	private boolean cancelled;
	private final ItemEntity entity;
	private final BakedModel model;
	private int returnValue;
	private int x, y, z;

	public ItemEntityRenderEvent(ItemEntity entity, BakedModel model, int x, int y, int z, float tickDelta) {
		super(tickDelta);
		this.entity = entity;
		this.model = model;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public void setReturnValue(int value) {
		returnValue = value;
		cancel();
	}

}
