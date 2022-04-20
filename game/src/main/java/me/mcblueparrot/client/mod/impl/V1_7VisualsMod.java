/**
 * Credit for OrangeMarshall for original mod.
 * It helped a lot when making this.
 */

package me.mcblueparrot.client.mod.impl;

import com.google.gson.annotations.Expose;

import me.mcblueparrot.client.event.EventHandler;
import me.mcblueparrot.client.event.impl.PreRenderTickEvent;
import me.mcblueparrot.client.event.impl.PreTickEvent;
import me.mcblueparrot.client.event.impl.TransformFirstPersonItemEvent;
import me.mcblueparrot.client.mod.Mod;
import me.mcblueparrot.client.mod.ModCategory;
import me.mcblueparrot.client.mod.annotation.Option;
import me.mcblueparrot.client.util.access.AccessEntityLivingBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.util.MovingObjectPosition;

public class V1_7VisualsMod extends Mod {

	@Expose
	@Option
	public boolean useAndMine = true;
	@Expose
	@Option
	private boolean particles = true;
	@Expose
	@Option
	public boolean items = true;
	@Expose
	@Option
	public boolean armourDamage = true;
	@Expose
	@Option
	public boolean sneaking = true;

	public static V1_7VisualsMod instance;
	public static boolean enabled;

	@Override
	public String getId() {
		return "1.7_visuals";
	}

	@Override
	public ModCategory getCategory() {
		return ModCategory.VISUAL;
	}

	@Override
	public void onRegister() {
		super.onRegister();
		instance = this;
	}

	@Override
	protected void onEnable() {
		super.onEnable();
		enabled = true;
	}

	@Override
	protected void onDisable() {
		super.onDisable();
		enabled = false;
	}

	@Override
	public boolean isEnabledByDefault() {
		return true;
	}

	@EventHandler
	public void onTick(PreTickEvent event) {
		if(mc.thePlayer != null && mc.thePlayer.capabilities.allowEdit
				&& isEnabled() && useAndMine && mc.objectMouseOver != null
				&& mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK
				&& mc.thePlayer != null
				&& mc.gameSettings.keyBindAttack.isKeyDown() && mc.gameSettings.keyBindUseItem.isKeyDown()
				&& mc.thePlayer.getItemInUseCount() > 0) {
			if((!mc.thePlayer.isSwingInProgress
				|| mc.thePlayer.swingProgressInt >= ((AccessEntityLivingBase) mc.thePlayer).accessArmSwingAnimationEnd()
				/ 2 || mc.thePlayer.swingProgressInt < 0)) {
				mc.thePlayer.swingProgressInt = -1;
				mc.thePlayer.isSwingInProgress = true;
			}

			if(particles) {
				mc.effectRenderer.addBlockHitEffects(mc.objectMouseOver.getBlockPos(), mc.objectMouseOver.sideHit);
			}
		}
	}

	@EventHandler
	public void onItemTransform(TransformFirstPersonItemEvent event) {
		if(!items) {
			return;
		}

		// https://github.com/sp614x/optifine/issues/2098

		if(mc.thePlayer.isUsingItem() && event.itemToRender.getItem() instanceof ItemBow) {
			GlStateManager.translate(-0.01f, 0.05f, -0.06f);
		}
		else if(event.itemToRender.getItem() instanceof ItemFishingRod) {
			GlStateManager.translate(0.08f, -0.027f, -0.33f);
			GlStateManager.scale(0.93f, 1.0f, 1.0f);
		}
	}

}
