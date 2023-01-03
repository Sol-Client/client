package io.github.solclient.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.solclient.client.Client;
import io.github.solclient.client.event.impl.ItemPickupEvent;
import io.github.solclient.client.util.access.AccessEntityLivingBase;
import net.minecraft.entity.*;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;

@Mixin(EntityLivingBase.class)
public abstract class MixinEntityLivingBase implements AccessEntityLivingBase {

	@Invoker("getArmSwingAnimationEnd")
	public abstract int accessArmSwingAnimationEnd();

	@Inject(method = "onItemPickup", at = @At("HEAD"))
	public void onItemPickup(Entity entity, int stackSize, CallbackInfo callback) {
		if (entity instanceof EntityItem && (Object) this instanceof EntityPlayer) {
			Client.INSTANCE.bus.post(new ItemPickupEvent((EntityPlayer) (Object) this, (EntityItem) entity));
		}
	}

}
