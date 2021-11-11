package me.mcblueparrot.client.mixin;

import me.mcblueparrot.client.Client;
import me.mcblueparrot.client.events.ItemPickupEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import me.mcblueparrot.client.util.access.AccessEntityLivingBase;
import net.minecraft.entity.EntityLivingBase;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityLivingBase.class)
public abstract class MixinEntityLivingBase implements AccessEntityLivingBase {

    @Invoker("getArmSwingAnimationEnd")
    public abstract int accessArmSwingAnimationEnd();

    @Inject(method = "onItemPickup", at = @At("HEAD"))
    public void onItemPickup(Entity entity, int stackSize, CallbackInfo callback) {
        if(entity instanceof EntityItem) {
            Client.INSTANCE.bus.post(new ItemPickupEvent((EntityItem) entity));
        }
    }

}
