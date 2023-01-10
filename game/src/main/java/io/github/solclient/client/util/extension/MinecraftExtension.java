package io.github.solclient.client.util.extension;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.DefaultResourcePack;
import net.minecraft.client.resources.data.IMetadataSerializer;
import net.minecraft.util.Timer;

public interface MinecraftExtension {

	boolean isRunning();

	Timer getTimerSC();

	DefaultResourcePack getDefaultResourcePack();

	IMetadataSerializer getMetadataSerialiser();

	void resizeWindow(int width, int height);

	static MinecraftExtension getInstance() {
		return (MinecraftExtension) Minecraft.getMinecraft();
	}

}
