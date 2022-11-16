package io.github.solclient.client.v1_19_2.mixins.platform.mc.resource;

import java.io.*;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.*;

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
