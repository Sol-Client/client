package me.mcblueparrot.client.util.access;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.DefaultResourcePack;
import net.minecraft.client.resources.data.IMetadataSerializer;
import net.minecraft.util.Timer;


// For some reason you do need a surrogate duck.
public interface AccessMinecraft {

	boolean isRunning();

	Timer getTimerSC();

	DefaultResourcePack getDefaultResourcePack();

	IMetadataSerializer getMetadataSerialiser();

	void resizeWindow(int width, int height);

	static AccessMinecraft getInstance() {
		return (AccessMinecraft) Minecraft.getMinecraft();
	}

}
