package me.mcblueparrot.client.events;

import lombok.AllArgsConstructor;
import net.minecraft.entity.item.EntityItem;

@AllArgsConstructor
public class ItemPickupEvent {

    public EntityItem pickedUp;

}
