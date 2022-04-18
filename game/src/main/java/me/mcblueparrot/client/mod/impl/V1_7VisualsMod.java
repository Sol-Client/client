/**
 * Credit for OrangeMarshall for original mod.
 * It helped a lot when making this.
 */

package me.mcblueparrot.client.mod.impl;

import com.google.gson.annotations.Expose;

import me.mcblueparrot.client.event.EventHandler;
import me.mcblueparrot.client.event.impl.TransformFirstPersonItemEvent;
import me.mcblueparrot.client.mod.Mod;
import me.mcblueparrot.client.mod.ModCategory;
import me.mcblueparrot.client.mod.annotation.Option;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemFishingRod;

public class V1_7VisualsMod extends Mod {

	@Expose
	@Option
	public boolean useAndMine = true;
	@Expose
	@Option
	public boolean rod = true;
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
	public void onItemTransform(TransformFirstPersonItemEvent event) {
		if(event.itemToRender.getItem() instanceof ItemFishingRod && rod) {
			GlStateManager.translate(-0.16F, 0.1F, 0);
		}
	}

}
