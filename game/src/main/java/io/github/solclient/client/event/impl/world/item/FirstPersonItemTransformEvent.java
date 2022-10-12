package io.github.solclient.client.event.impl.world.item;

import io.github.solclient.client.platform.mc.world.item.ItemStack;
import lombok.*;

@Data
@RequiredArgsConstructor
public class FirstPersonItemTransformEvent {

	private final ItemStack item;

}
