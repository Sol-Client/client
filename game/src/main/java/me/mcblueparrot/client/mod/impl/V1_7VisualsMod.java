/**
 * Credit for OrangeMarshall for original mod, and most of the code (excluding the sneak).
 */

package me.mcblueparrot.client.mod.impl;

import com.google.gson.annotations.Expose;

import me.mcblueparrot.client.event.EventHandler;
import me.mcblueparrot.client.event.impl.TransformFirstPersonItemEvent;
import me.mcblueparrot.client.mod.Mod;
import me.mcblueparrot.client.mod.ModCategory;
import me.mcblueparrot.client.mod.annotation.ConfigOption;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemFishingRod;

public class V1_7VisualsMod extends Mod {

	@Expose
	@ConfigOption("Use Item while Mining")
	public boolean useAndMine = true;
	@Expose
	@ConfigOption("Fishing Rod")
	public boolean rod = true;
	@Expose
	@ConfigOption("Armour Damage")
	public boolean armourDamage = true;
	@Expose
	@ConfigOption("Sneaking")
	public boolean sneaking = true;
	public static V1_7VisualsMod instance;
	public static boolean enabled;

	public V1_7VisualsMod() {
		super("1.7 Visuals", "1.7_visuals", "Brings back some of the look-and-feel of 1.7.", ModCategory.VISUAL);
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
