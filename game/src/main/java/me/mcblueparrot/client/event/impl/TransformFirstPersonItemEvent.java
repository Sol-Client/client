package me.mcblueparrot.client.event.impl;

import lombok.AllArgsConstructor;
import net.minecraft.item.ItemStack;

@AllArgsConstructor
public class TransformFirstPersonItemEvent {

	public ItemStack itemToRender;
	public float equipProgress;
	public float swingProgress;

}
