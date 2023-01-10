package io.github.solclient.client.util.extension;

import net.minecraft.entity.Entity;

public interface RenderExtension<T extends Entity> {

	void doRenderName(T entity, double x, double y, double z);

}
