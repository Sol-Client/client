package me.mcblueparrot.client.mixin;

import me.mcblueparrot.client.Client;
import me.mcblueparrot.client.events.EntityAttackEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityPlayer.class)
public class MixinEntityPlayer {

    @Inject(method = "attackTargetEntityWithCurrentItem", at = @At("HEAD"))
    public void attackEntity(Entity entity, CallbackInfo callback) {
        if(entity.canAttackWithItem()) {
            Client.INSTANCE.bus.post(new EntityAttackEvent(entity));
        }
    }

}
