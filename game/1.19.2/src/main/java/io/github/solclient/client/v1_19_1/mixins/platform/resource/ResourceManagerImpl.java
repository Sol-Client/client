package io.github.solclient.client.v1_19_1.mixins.platform.resource;

import java.io.FileNotFoundException;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import io.github.solclient.client.platform.mc.resource.Identifier;
import io.github.solclient.client.platform.mc.resource.Resource;
import io.github.solclient.client.platform.mc.resource.ResourceManager;

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
