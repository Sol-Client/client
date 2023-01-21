package io.github.solclient.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.solclient.client.Client;
import io.github.solclient.client.event.impl.ItemPickupEvent;
import io.github.solclient.client.util.extension.LivingEntityExtension;
import net.minecraft.entity.*;
import net.minecraft.entity.player.PlayerEntity;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity implements LivingEntityExtension {

	@Invoker("getMiningSpeedMultiplier")
	public abstract int privateGetArmSwingAnimationEnd();

	@Inject(method = "sendPickup", at = @At("HEAD"))
	public void sendPickup(Entity entity, int stackSize, CallbackInfo callback) {
		if (entity instanceof ItemEntity && (Object) this instanceof PlayerEntity)
			Client.INSTANCE.getEvents().post(new ItemPickupEvent((PlayerEntity) (Object) this, (ItemEntity) entity));
	}

}
