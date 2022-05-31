package io.github.solclient.client.event.impl;

import lombok.RequiredArgsConstructor;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.entity.item.EntityItem;

@RequiredArgsConstructor
public class ItemEntityRenderEvent {
	
	public boolean cancelled;
	public final EntityItem entity;
	public final double x;
	public final double y;
	public final double z;
	public final float partialTicks;
	public final IBakedModel model;
	public int result = -1;

}
