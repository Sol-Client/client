package io.github.solclient.client.mixin.mod;

import org.spongepowered.asm.mixin.Mixin;

import io.github.solclient.client.mod.impl.TNTTimerMod;
import net.minecraft.client.render.entity.*;
import net.minecraft.entity.TntEntity;

public class MixinTNTTimerMod {

	@Mixin(TntEntityRenderer.class)
	public static abstract class MixinTntEntityRenderer extends EntityRenderer<TntEntity> {

		protected MixinTntEntityRenderer(EntityRenderDispatcher dispatcher) {
			super(dispatcher);
		}

		@Override
		protected void method_10208(TntEntity tnt, double x, double y, double z) {
			if (TNTTimerMod.enabled)
				renderLabelIfPresent(tnt, TNTTimerMod.getText(tnt), x, y, z, 64);
		}

	}

}
