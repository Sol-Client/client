package io.github.solclient.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

import io.github.solclient.client.Client;
import io.github.solclient.client.event.impl.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerEntity.SleepStatus;
import net.minecraft.util.math.BlockPos;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {

	@Inject(method = "attack", at = @At("HEAD"))
	public void attackEntity(Entity entity, CallbackInfo callback) {
		if (entity.isAttackable()) {
			Client.INSTANCE.getEvents().post(new EntityAttackEvent(entity));
		}
	}

	@Inject(method = "attemptSleep", at = @At("HEAD"))
	public void onSleep(BlockPos pos, CallbackInfoReturnable<SleepStatus> callback) {
		Client.INSTANCE.getEvents().post(new PlayerSleepEvent((PlayerEntity) (Object) this, pos));
	}

}
