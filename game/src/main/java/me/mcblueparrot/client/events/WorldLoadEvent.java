package me.mcblueparrot.client.events;

import net.minecraft.client.multiplayer.WorldClient;

public class WorldLoadEvent {

    public WorldClient world;

    public WorldLoadEvent(WorldClient world) {
        this.world = world;
    }

}
