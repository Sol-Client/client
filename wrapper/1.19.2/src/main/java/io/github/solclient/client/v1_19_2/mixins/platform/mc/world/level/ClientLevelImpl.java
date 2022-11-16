package io.github.solclient.client.v1_19_2.mixins.platform.mc.world.level;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.*;

import io.github.solclient.client.platform.mc.world.entity.Entity;
import io.github.solclient.client.platform.mc.world.level.ClientLevel;
import net.minecraft.client.world.ClientWorld;

@Mixin(ClientWorld.class)
public abstract class ClientLevelImpl implements ClientLevel {

	@Override
	public @NotNull Iterable<Entity> getRenderedEntities() {
		return getEntities();
	}

	@Shadow
	public abstract Iterable<Entity> getEntities();

}
