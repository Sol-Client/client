package me.mcblueparrot.client.util.access;

import net.minecraft.entity.Entity;

public interface AccessRender<T extends Entity> {

	void doRenderName(T entity, double x, double y, double z);

}
