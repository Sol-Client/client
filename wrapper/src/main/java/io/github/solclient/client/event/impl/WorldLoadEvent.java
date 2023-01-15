package io.github.solclient.client.event.impl;

import lombok.AllArgsConstructor;
import net.minecraft.client.world.ClientWorld;

@AllArgsConstructor
public class WorldLoadEvent {

	public final ClientWorld world;

}
