package me.mcblueparrot.client.mixin.mod;

import org.spongepowered.asm.mixin.Mixin;

import me.mcblueparrot.client.mod.impl.TNTTimerMod;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderTNTPrimed;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.util.EnumChatFormatting;

public class MixinTNTTimerMod {

	@Mixin(RenderTNTPrimed.class)
	public static abstract class MixinRenderTNTPrimed extends Render<EntityTNTPrimed> {

		protected MixinRenderTNTPrimed(RenderManager renderManager) {
			super(renderManager);
		}

		@Override
		protected void renderName(EntityTNTPrimed tnt, double x, double y, double z) {
			if(TNTTimerMod.enabled) {
				renderLivingLabel(tnt, TNTTimerMod.getText(tnt), x, y, z, 64);
			}
		}

	}

}
