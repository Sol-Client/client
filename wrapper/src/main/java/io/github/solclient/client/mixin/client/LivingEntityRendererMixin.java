package io.github.solclient.client.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import io.github.solclient.client.Client;
import io.github.solclient.client.event.impl.HitOverlayEvent;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.entity.LivingEntity;

@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererMixin<T extends LivingEntity> {

	private static float sc$red;
	private static float sc$green;
	private static float sc$blue;
	private static float sc$alpha;

	@Inject(method = "method_10252", at = @At("HEAD"))
	public void initHitColour(T entitylivingbaseIn, float partialTicks, boolean combineTextures,
			CallbackInfoReturnable<Boolean> callback) {
		HitOverlayEvent event = new HitOverlayEvent(1, 0, 0, 0.3F);
		Client.INSTANCE.getEvents().post(event);

		sc$red = event.r;
		sc$green = event.g;
		sc$blue = event.b;
		sc$alpha = event.a;
	}

	@ModifyConstant(method = "method_10252", constant = @Constant(floatValue = 1, ordinal = 0))
	public float overrideHitColourR(float original) {
		return sc$red;
	}

	@ModifyConstant(method = "method_10252", constant = @Constant(floatValue = 0, ordinal = 0))
	public float overrideHitColourG(float original) {
		return sc$green;
	}

	@ModifyConstant(method = "method_10252", constant = @Constant(floatValue = 0, ordinal = 1))
	public float overrideHitColourB(float original) {
		return sc$blue;
	}

	@ModifyConstant(method = "method_10252", constant = @Constant(floatValue = 0.3F, ordinal = 0))
	public float overrideHitColourA(float original) {
		return sc$alpha;
	}

}
