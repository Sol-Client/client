package io.github.solclient.client.v1_8_9.mixins.platform.mc.resource;

import java.io.InputStream;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.*;

import io.github.solclient.client.platform.mc.resource.Resource;

@Mixin(net.minecraft.resource.Resource.class)
public abstract interface ResourceImpl extends Resource {

	@Override
	default @NotNull InputStream getInput() {
		return getInputStream();
	}

	@Shadow
	public abstract InputStream getInputStream();
}
