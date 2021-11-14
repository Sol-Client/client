package me.mcblueparrot.client;

import lombok.AllArgsConstructor;
import net.minecraft.client.multiplayer.ServerData;

@AllArgsConstructor
public class ServerConnectEvent {

    public ServerData data;
    public DetectedServer server;

}
