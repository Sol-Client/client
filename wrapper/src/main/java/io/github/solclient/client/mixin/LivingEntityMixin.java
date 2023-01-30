package io.github.solclient.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.solclient.client.Client;
import io.github.solclient.client.event.impl.ItemPickupEvent;
import io.github.solclient.client.mixin.client.LivingEntityAccessor;
import net.minecraft.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

	@Inject(method = "sendPickup", at = @At("HEAD"))
	public void sendPickup(Entity entity, int stackSize, CallbackInfo callback) {
		if (entity instanceof ItemEntity && (Object) this instanceof PlayerEntity)
			Client.INSTANCE.getEvents().post(new ItemPickupEvent((PlayerEntity) (Object) this, (ItemEntity) entity));
	}

}
