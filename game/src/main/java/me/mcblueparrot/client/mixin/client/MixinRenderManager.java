package me.mcblueparrot.client.mixin.client;

import me.mcblueparrot.client.Cullable;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderEntity;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RenderManager.class)
public abstract class MixinRenderManager {

    @Shadow public TextureManager renderEngine;

    @Shadow public abstract <T extends Entity> Render<T> getEntityRenderObject(Entity entityIn);


    @Inject(method = "doRenderEntity", at = @At("HEAD"), cancellable = true)
    public void cullEntity(Entity entity, double x, double y, double z, float entityYaw, float partialTicks,
                           boolean hideDebugBox, CallbackInfoReturnable<Boolean> callback) {
        if(((Cullable) entity).isCulled()) {
            callback.setReturnValue(renderEngine == null);
        }
    }

}
