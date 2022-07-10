package io.github.solclient.client.mod.impl.hud.timers;

import io.github.solclient.client.platform.mc.world.item.ItemStack;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * TODO: Move to ClientApi.
 */
@Data
@RequiredArgsConstructor
public class Timer {

	private final String name;
	private final ItemStack renderItem;
	private long id;
	private long time;

	public Timer(String name, ItemStack renderItem, long startTime) {
		this(name, renderItem);
		time = startTime;
	}

	public void tick() {
		if(time > 0) {
			time--;
		}
	}

}
