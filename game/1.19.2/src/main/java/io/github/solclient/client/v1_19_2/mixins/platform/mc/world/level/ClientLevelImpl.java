package io.github.solclient.client.v1_19_2.mixins.platform.mc.world.level;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import io.github.solclient.client.platform.mc.world.entity.Entity;
import io.github.solclient.client.platform.mc.world.level.ClientLevel;
import net.minecraft.client.world.ClientWorld;

@Mixin(ClientWorld.class)
public abstract class ClientLevelImpl implements ClientLevel {

	@Override
	public @NotNull Iterable<Entity> getRenderedEntities() {
		// oh wait type erasure moment we could have just used the correct type in the
		// shadow field moment
		return (Iterable) getEntities();
	}

	@Shadow
	public abstract Iterable<net.minecraft.entity.Entity> getEntities();

}
