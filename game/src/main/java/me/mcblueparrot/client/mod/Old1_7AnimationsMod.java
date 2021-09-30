/**
 * Credit for OrangeMarshall for original mod, and most of the code.
 */

package me.mcblueparrot.client.mod;

import com.google.gson.annotations.Expose;

import me.mcblueparrot.client.events.EventHandler;
import me.mcblueparrot.client.events.TickEvent;
import me.mcblueparrot.client.events.TransformFirstPersonItemEvent;
import me.mcblueparrot.client.mod.annotation.ConfigOption;
import me.mcblueparrot.client.util.access.AccessEntityLivingBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemFishingRod;

public class Old1_7AnimationsMod extends Mod {

    @Expose
    @ConfigOption("Use and Mine")
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
    public static Old1_7AnimationsMod instance;
    public static boolean enabled;

    public Old1_7AnimationsMod() {
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
