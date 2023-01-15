package io.github.solclient.client.mixin.mod;

import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.TntEntityRenderer;
import net.minecraft.entity.TntEntity;
import org.spongepowered.asm.mixin.Mixin;

import io.github.solclient.client.mod.impl.TNTTimerMod;

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
