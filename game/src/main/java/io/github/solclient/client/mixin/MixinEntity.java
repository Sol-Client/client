package io.github.solclient.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import io.github.solclient.client.culling.Cullable;
import io.github.solclient.client.util.extension.EntityExtension;
import lombok.*;
import net.minecraft.entity.Entity;

@Mixin(Entity.class)
public abstract class MixinEntity implements Cullable, EntityExtension {

	@Getter
	@Setter
	private boolean culled;

	@Accessor("isInWeb")
	public abstract boolean getIsInWeb();

}
