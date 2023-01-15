package io.github.solclient.client.event.impl;

import lombok.AllArgsConstructor;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.*;

@AllArgsConstructor
public class ItemPickupEvent {

	public final PlayerEntity player;
	public final ItemEntity pickedUp;

}
