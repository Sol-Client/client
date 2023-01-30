package io.github.solclient.client.extension;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ServerInfo;

public interface MinecraftClientExtension {

	static MinecraftClientExtension getInstance() {
		return (MinecraftClientExtension) MinecraftClient.getInstance();
	}

	ServerInfo getPreviousServer();

}
