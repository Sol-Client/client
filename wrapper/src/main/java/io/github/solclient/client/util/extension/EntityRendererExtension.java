package io.github.solclient.client.util.extension;

import net.minecraft.entity.Entity;

public interface EntityRendererExtension<T extends Entity> {

	void renderName(T entity, double x, double y, double z);

}
