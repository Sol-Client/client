package me.mcblueparrot.client.events;

import lombok.AllArgsConstructor;
import net.minecraft.item.ItemStack;

@AllArgsConstructor
public class TransformFirstPersonItemEvent {

    public ItemStack itemToRender;
    public float equipProgress;
    public float swingProgress;

}
