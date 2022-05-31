package io.github.solclient.client.event.impl;

import lombok.AllArgsConstructor;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;

@AllArgsConstructor
public class ItemPickupEvent {

	public final EntityPlayer player;
	public final EntityItem pickedUp;

}
