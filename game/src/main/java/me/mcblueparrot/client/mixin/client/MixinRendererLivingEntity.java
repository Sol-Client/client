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

    private static float red;
    private static float green;
    private static float blue;
    private static float alpha;

    @Inject(method = "setBrightness", at = @At("HEAD"))
    public void initHitColour(T entitylivingbaseIn, float partialTicks, boolean combineTextures,
                              CallbackInfoReturnable<Boolean> callback) {
        HitOverlayEvent event = new HitOverlayEvent(1, 0, 0, 0.3F);
        Client.INSTANCE.bus.post(event);

        red = event.r;
        green = event.g;
        blue = event.b;
        alpha = event.a;
    }

    @ModifyConstant(method = "setBrightness", constant = @Constant(floatValue = 1, ordinal = 0))
    public float overrideHitColourR(float original) {
        return red;
    }

    @ModifyConstant(method = "setBrightness", constant = @Constant(floatValue = 0, ordinal = 0))
    public float overrideHitColourG(float original) {
        return green;
    }

    @ModifyConstant(method = "setBrightness", constant = @Constant(floatValue = 0, ordinal = 1))
    public float overrideHitColourB(float original) {
        return blue;
    }

    @ModifyConstant(method = "setBrightness", constant = @Constant(floatValue = 0.3F, ordinal = 0))
    public float overrideHitColourA(float original) {
        return alpha;
    }

}
