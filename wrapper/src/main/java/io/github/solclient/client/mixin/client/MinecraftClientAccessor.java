package io.github.solclient.client.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.*;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.ClientTickTracker;
import net.minecraft.resource.DefaultResourcePack;
import net.minecraft.util.MetadataSerializer;

@Mixin(MinecraftClient.class)
public interface MinecraftClientAccessor {

	@Accessor
	boolean isRunning();

	@Accessor
	ClientTickTracker getTicker();

	@Accessor
	DefaultResourcePack getDefaultResourcePack();

	@Accessor("metadataSerializer")
	MetadataSerializer getMetadataSerialiser();

	@Invoker("onResolutionChanged")
	void resizeWindow(int width, int height);

}
