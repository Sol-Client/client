package io.github.solclient.client.v1_8_9.mixins.platform.mc.resource;

import java.io.IOException;
import java.util.*;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.*;

import io.github.solclient.client.platform.mc.resource.*;

@Mixin(net.minecraft.resource.ResourceManager.class)
public interface ResourceManagerImpl extends ResourceManager {

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	default @NotNull List<Resource> getResources(Identifier id) {
		try {
			return (List) getAllResources((net.minecraft.util.Identifier) id);
		}
		catch(IOException ignored) {
			// For compatibility with 1.19
			return Collections.emptyList();
		}
	}

	@Shadow
	List<net.minecraft.resource.Resource> getAllResources(net.minecraft.util.Identifier id) throws IOException;

}
