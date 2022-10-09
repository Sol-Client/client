package io.github.solclient.client.v1_8_9.mixins.platform.mc.world.level;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;

import io.github.solclient.client.platform.mc.world.entity.Entity;
import io.github.solclient.client.platform.mc.world.level.ClientLevel;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.SaveHandler;
import net.minecraft.world.World;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.level.LevelProperties;

@Mixin(ClientWorld.class)
@Implements(@Interface(iface = ClientLevel.class, prefix = "platform$"))
public abstract class ClientLevelImpl extends World {

	protected ClientLevelImpl(SaveHandler handler, LevelProperties properties, Dimension dim, Profiler profiler,
			boolean isClient) {
		super(handler, properties, dim, profiler, isClient);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public @NotNull Iterable<Entity> platform$getRenderedEntities() {
		return (List) loadedEntities;
	}

}
