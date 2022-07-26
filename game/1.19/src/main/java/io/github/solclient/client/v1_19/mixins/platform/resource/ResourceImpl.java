package io.github.solclient.client.v1_19.mixins.platform.resource;

import java.io.IOException;
import java.io.InputStream;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import io.github.solclient.client.platform.mc.resource.Resource;

@Mixin(net.minecraft.resource.Resource.class)
public abstract class ResourceImpl implements Resource {

	@Override
	public @NotNull InputStream getInput() throws IOException {
		return getInputStream();
	}

	@Shadow
	public abstract InputStream getInputStream() throws IOException;
}
