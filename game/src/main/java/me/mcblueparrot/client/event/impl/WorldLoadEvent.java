package me.mcblueparrot.client.event.impl;

import lombok.AllArgsConstructor;
import net.minecraft.client.multiplayer.WorldClient;

@AllArgsConstructor
public class WorldLoadEvent {

	public final WorldClient world;

}
