package io.github.solclient.client.extension;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.ClientTickTracker;
import net.minecraft.resource.DefaultResourcePack;
import net.minecraft.util.MetadataSerializer;

public interface MinecraftClientExtension {

	static MinecraftClientExtension getInstance() {
		return (MinecraftClientExtension) MinecraftClient.getInstance();
	}

	boolean isRunning();

	ClientTickTracker getTicker();

	DefaultResourcePack getDefaultResourcePack();

	MetadataSerializer getMetadataSerialiser();

	void resizeWindow(int width, int height);

}
