package io.github.solclient.client.event.impl.world.entity.render;

import io.github.solclient.abstraction.mc.model.BakedModel;
import io.github.solclient.abstraction.mc.world.entity.item.ItemEntity;
import io.github.solclient.client.event.Cancellable;
import lombok.Data;

@Data
public class ItemEntityRenderEvent implements Cancellable {

	private boolean cancelled;
	private ItemEntity entity;
	private int returnValue;
	private BakedModel model;
	private int x, y, z;

	public void setReturnValue(int value) {
		returnValue = value;
		cancel();
	}

}
