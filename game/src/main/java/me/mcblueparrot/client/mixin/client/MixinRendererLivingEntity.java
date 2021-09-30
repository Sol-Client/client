package me.mcblueparrot.client.mixin.client;

import me.mcblueparrot.client.Client;
import me.mcblueparrot.client.events.HitOverlayEvent;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.EntityLivingBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RendererLivingEntity.class)
public class MixinRendererLivingEntity<T extends EntityLivingBase> {

    private static float r;
    private static float g;
    private static float b;
    private static float a;

    @Inject(method = "setBrightness", at = @At("HEAD"))
    public void initHitColour(T entitylivingbaseIn, float partialTicks, boolean combineTextures,
                              CallbackInfoReturnable<Boolean> callback) {
        HitOverlayEvent event = new HitOverlayEvent(1, 0, 0, 0.3F);
        Client.INSTANCE.bus.post(event);

        r = event.r;
        g = event.g;
        b = event.b;
        a = event.a;
    }

    @ModifyConstant(method = "setBrightness", constant = @Constant(floatValue = 1, ordinal = 0))
    public float overrideHitColourR(float original) {
        return r;
    }

    @ModifyConstant(method = "setBrightness", constant = @Constant(floatValue = 0, ordinal = 0))
    public float overrideHitColourG(float original) {
        return g;
    }

    @ModifyConstant(method = "setBrightness", constant = @Constant(floatValue = 0, ordinal = 1))
    public float overrideHitColourB(float original) {
        return b;
    }

    @ModifyConstant(method = "setBrightness", constant = @Constant(floatValue = 0.3F, ordinal = 0))
    public float overrideHitColourA(float original) {
        return a;
    }

}
