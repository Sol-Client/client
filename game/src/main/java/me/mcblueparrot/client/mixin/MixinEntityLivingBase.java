package me.mcblueparrot.client.mixin;

import me.mcblueparrot.client.util.access.AccessEntityLivingBase;
import net.minecraft.entity.EntityLivingBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(EntityLivingBase.class)
public abstract class MixinEntityLivingBase implements AccessEntityLivingBase {

    @Invoker("getArmSwingAnimationEnd")
    public abstract int accessArmSwingAnimationEnd();

}
