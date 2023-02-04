package io.github.solclient.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import io.github.solclient.client.culling.Cullable;
import lombok.*;
import net.minecraft.entity.Entity;

@Mixin(Entity.class)
public abstract class EntityMixin implements Cullable {

	@Getter
	@Setter
	private boolean culled;

}
