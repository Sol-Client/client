/**
 * Credit for OrangeMarshall for original mod.
 * It helped a lot when making this.
 */

package io.github.solclient.client.mod.impl;

import com.google.gson.annotations.Expose;

import io.github.solclient.client.event.EventHandler;
import io.github.solclient.client.event.impl.game.PreTickEvent;
import io.github.solclient.client.event.impl.world.item.FirstPersonItemTransformEvent;
import io.github.solclient.client.mod.Mod;
import io.github.solclient.client.mod.ModCategory;
import io.github.solclient.client.mod.annotation.Option;
import io.github.solclient.client.platform.mc.raycast.HitType;
import io.github.solclient.client.platform.mc.render.GlStateManager;
import io.github.solclient.client.platform.mc.world.entity.player.ClientPlayer;
import io.github.solclient.client.platform.mc.world.item.ItemStack;
import io.github.solclient.client.platform.mc.world.item.ItemType;

public class V1_7VisualsMod extends Mod {

	public static final V1_7VisualsMod INSTANCE = new V1_7VisualsMod();

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

	@Override
	public String getId() {
		return "1.7_visuals";
	}

	@Override
	public ModCategory getCategory() {
		return ModCategory.VISUAL;
	}

	@Override
	public boolean isEnabledByDefault() {
		return true;
	}

	@EventHandler
	public void onTick(PreTickEvent event) {
		if(mc.hasPlayer() && mc.getPlayer().getAbilities().canBuild() && isEnabled() && useAndMine
				&& mc.getHitResult().getType() == HitType.BLOCK && mc.getOptions().attackKey().isHeld()
				&& mc.getOptions().useKey().isHeld() && mc.getPlayer().getItemUsageRemaining() > 0) {
			mc.getPlayer().clientSwing();

			if(particles) {
				mc.getParticleEngine().emitDestruction(mc.getHitResult().getBlockPos(), mc.getHitResult().getBlockSide());
			}
		}
	}

	@EventHandler
	public void onItemTransform(FirstPersonItemTransformEvent event) {
		if(!(bow || rod)) {
			return;
		}

		// https://github.com/sp614x/optifine/issues/2098
		if(mc.getPlayer().isUsingItem() && event.getItem().getType() == ItemType.BOW && bow) {
			GlStateManager.translate(-0.01F, 0.05F, -0.06F);
		}
		else if(event.getItem().getType() == ItemType.FISHING_ROD && rod) {
			GlStateManager.translate(0.08F, -0.027F, -0.33F);
			GlStateManager.scale(0.93F, 1.0F, 1.0F);
		}
	}

	public static void oldDrinking(ItemStack item, ClientPlayer player, float tickDelta) {
		float var14 = player.getItemUsageRemaining() - tickDelta + 1.0F;
		float var15 = 1.0F - var14 / item.getMaxItemUseTime();
		float var16 = 1.0F - var15;

		for(int i = 0; i < 3; i++)
			var16 = (float) Math.pow(var16, 3);

		var16 -= 0.05F;
		float var17 = 1.0F - var16;
		GlStateManager.translate(0.0F,
				Math.abs(Math.cos(var14 / 4F * (float) Math.PI) * 0.11F) * (float) ((double) var15 > 0.2D ? 1 : 0),
				0.0F);
		GlStateManager.translate(var17 * 0.6F, -var17 * 0.5F, 0.0F);
		GlStateManager.rotate(var17 * 90.0F, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotate(var17 * 10.0F, 1.0F, 0.0F, 0.0F);
		GlStateManager.rotate(var17 * 30.0F, 0.0F, 0.0F, 1.0F);
		GlStateManager.translate(0, -0.0F, 0.06F);
		GlStateManager.rotate(-4F, 1, 0, 0);
	}

	public static void oldBlocking() {
		// https://github.com/TAKfsg/oldblockhit-legacy-fabric
		GlStateManager.scale(0.83F, 0.88F, 0.85F);
		GlStateManager.translate(-0.3F, 0.1F, 0.0F);
	}

}
