package io.github.solclient.client.event.impl;

import lombok.AllArgsConstructor;
import net.minecraft.item.ItemStack;

@AllArgsConstructor
public class TransformFirstPersonItemEvent {

	public final ItemStack itemToRender;
	public final float equipProgress;
	public final float swingProgress;

}
