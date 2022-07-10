package io.github.solclient.client.event.impl.world.item;

import io.github.solclient.abstraction.mc.world.item.ItemStack;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class FirstPersonItemTransformEvent {

	private final ItemStack item;

}
