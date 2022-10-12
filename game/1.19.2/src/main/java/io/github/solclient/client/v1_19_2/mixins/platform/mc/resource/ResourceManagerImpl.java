package io.github.solclient.client.v1_19_2.mixins.platform.mc.resource;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.*;

import io.github.solclient.client.platform.mc.resource.*;

@Mixin(net.minecraft.resource.ResourceManager.class)
public interface ResourceManagerImpl extends ResourceManager {

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	default @NotNull List<Resource> getResources(Identifier id) {
		return (List) getAllResources((net.minecraft.util.Identifier) id);
	}

	@Shadow
	List<net.minecraft.resource.Resource> getAllResources(net.minecraft.util.Identifier id);

}
