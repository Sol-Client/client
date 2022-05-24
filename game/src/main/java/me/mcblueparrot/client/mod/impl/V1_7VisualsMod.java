/**
 * Credit for OrangeMarshall for original mod.
 * It helped a lot when making this.
 */

package me.mcblueparrot.client.mod.impl;

import org.lwjgl.opengl.GL11;

import com.google.gson.annotations.Expose;

import me.mcblueparrot.client.event.EventHandler;
import me.mcblueparrot.client.event.impl.PreRenderTickEvent;
import me.mcblueparrot.client.event.impl.PreTickEvent;
import me.mcblueparrot.client.event.impl.TransformFirstPersonItemEvent;
import me.mcblueparrot.client.mod.Mod;
import me.mcblueparrot.client.mod.ModCategory;
import me.mcblueparrot.client.mod.annotation.Option;
import me.mcblueparrot.client.util.access.AccessEntityLivingBase;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
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
	public boolean blocking = true;
	@Expose
	@Option
	public boolean eatingAndDrinking = true;
	@Expose
	@Option
	private boolean bow = true;
	@Expose
	@Option
	private boolean rod = true;
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
		if(!(bow || rod)) {
			return;
		}

		// https://github.com/sp614x/optifine/issues/2098
		if(mc.thePlayer.isUsingItem() && event.itemToRender.getItem() instanceof ItemBow) {
			if(bow) {
				GlStateManager.translate(-0.01f, 0.05f, -0.06f);
			}
		}
		else if(event.itemToRender.getItem() instanceof ItemFishingRod) {
			if(rod) {
				GlStateManager.translate(0.08f, -0.027f, -0.33f);
				GlStateManager.scale(0.93f, 1.0f, 1.0f);
			}
		}
	}

	public static void oldDrinking(ItemStack itemToRender, AbstractClientPlayer clientPlayer, float partialTicks) {
		float var14 = clientPlayer.getItemInUseCount() - partialTicks + 1.0F;
		float var15 = 1.0F - var14 / itemToRender.getMaxItemUseDuration();
		float var16 = 1.0F - var15;
		var16 = var16 * var16 * var16;
		var16 = var16 * var16 * var16;
		var16 = var16 * var16 * var16;
		var16 -= 0.05F;
		float var17 = 1.0F - var16;
		GlStateManager.translate(0.0F, MathHelper.abs(MathHelper.cos(var14 / 4F * (float) Math.PI) * 0.1F)
				* (float) ((double) var15 > 0.2D ? 1 : 0), 0.0F);
		GlStateManager.translate(var17 * 0.6F, -var17 * 0.5F, 0.0F);
		GlStateManager.rotate(var17 * 90.0F, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotate(var17 * 10.0F, 1.0F, 0.0F, 0.0F);
		GlStateManager.rotate(var17 * 30.0F, 0.0F, 0.0F, 1.0F);
		GlStateManager.translate(0, -0.0F, 0.06F);
		GlStateManager.rotate(-4F, 1, 0, 0);
	}
		
	public static void oldBlocking() {
		GlStateManager.scale(0.83F, 0.88F, 0.85F);
		GlStateManager.translate(-0.3F, 0.1F, 0.0F);
	}

}
