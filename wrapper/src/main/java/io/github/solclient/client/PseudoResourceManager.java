package io.github.solclient.client;

import java.util.*;

import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;

public final class PseudoResourceManager {

	private final Map<ResourceLocation, IResource> map = new HashMap<>();

	public void register(ResourceLocation location, IResource resource) {
		map.put(location, resource);
	}

	public IResource get(ResourceLocation location) {
		return map.get(location);
	}

}
