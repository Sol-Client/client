package io.github.solclient.client.mixin.mod;

import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.solclient.client.mod.impl.TNTTimerMod;
import net.minecraft.client.render.entity.*;
import net.minecraft.entity.TntEntity;

public class TNTTimerModMixins {

	@Mixin(TntEntityRenderer.class)
	public static abstract class TntEntityRendererMixin extends EntityRenderer<TntEntity> {

		protected TntEntityRendererMixin(EntityRenderDispatcher dispatcher) {
			super(dispatcher);
		}

		// i may have followed the axolotl for this one...
		@Inject(method = "render(Lnet/minecraft/entity/TntEntity;DDDFF)V", at = @At("RETURN"))
		protected void method_10208(TntEntity tnt, double x, double y, double z, float yaw, float tickDelta, CallbackInfo callback) {
			if (TNTTimerMod.enabled)
				renderLabelIfPresent(tnt, TNTTimerMod.getText(tnt), x, y, z, 64);
		}

	}

}
