package io.github.solclient.client.mod.impl.tweaks.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.solclient.client.mod.impl.tweaks.TweaksMod;
import net.minecraft.block.BlockState;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.util.math.*;

@Mixin(ParticleManager.class)
public class ParticleManagerMixin {

	@Inject(method = "addBlockBreakingParticles", at = @At("HEAD"), cancellable = true)
	public void cancelBlockBreakingParticles(BlockPos pos, Direction direction, CallbackInfo callback) {
		if (TweaksMod.enabled && TweaksMod.instance.disableBlockParticles)
			callback.cancel();
	}

	@Inject(method = "addBlockBreakParticles", at = @At("HEAD"), cancellable = true)
	public void cancelBlockBreakParticles(BlockPos pos, BlockState state, CallbackInfo callback) {
		if (TweaksMod.enabled && TweaksMod.instance.disableBlockParticles)
			callback.cancel();
	}

}
