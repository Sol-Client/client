package io.github.solclient.client.v1_19_2.mixins.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.MinecraftClient;

@Mixin(MinecraftClient.class)
public interface MinecraftClientAccessor {

	@Accessor
	static int getCurrentFps() {
		throw new UnsupportedOperationException();
	}

}
