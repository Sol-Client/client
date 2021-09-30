package me.mcblueparrot.client;

import lombok.AllArgsConstructor;
import me.mcblueparrot.client.Client.DetectedServer;
import net.minecraft.client.multiplayer.ServerData;

@AllArgsConstructor
public class ServerChangeEvent {

    public DetectedServer server;

}
