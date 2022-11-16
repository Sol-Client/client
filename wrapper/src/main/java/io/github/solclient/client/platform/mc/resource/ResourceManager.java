package io.github.solclient.client.platform.mc.resource;

import java.util.List;

import org.jetbrains.annotations.NotNull;

public interface ResourceManager {

	@NotNull List<Resource> getResources(Identifier id);

}
