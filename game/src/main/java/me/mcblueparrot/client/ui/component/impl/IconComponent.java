package me.mcblueparrot.client.ui.component.impl;

import me.mcblueparrot.client.mod.impl.SolClientMod;
import me.mcblueparrot.client.ui.component.ComponentRenderInfo;
import me.mcblueparrot.client.ui.component.controller.Controller;
import me.mcblueparrot.client.util.Utils;
import me.mcblueparrot.client.util.data.Colour;
import me.mcblueparrot.client.util.data.Rectangle;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class IconComponent extends ColouredComponent {

    private Controller<ResourceLocation> icon;
    private int width;
    private int height;

    public IconComponent(ResourceLocation icon, int width, int height) {
        this((component, defaultName) -> icon, width, height, (component, defaultColour) -> defaultColour);
    }

    public IconComponent(ResourceLocation icon, int width, int height, Controller<Colour> colour) {
        this((component, defaultName) -> icon, width, height, colour);
    }

    public IconComponent(Controller<ResourceLocation> icon, int width, int height, Controller<Colour> colour) {
        super(colour);
        this.icon = icon;
        this.width = width;
        this.height = height;
    }


    public void renderFallback(ComponentRenderInfo info) {
    }

    public boolean useFallback() {
        return false;
    }

    @Override
    public void render(ComponentRenderInfo info) {
        if(useFallback() && !SolClientMod.instance.roundedUI) {
            renderFallback(info);
        }
        else {
            GlStateManager.enableAlpha();
            GlStateManager.enableBlend();

            Utils.glColour(getColour());

            mc.getTextureManager().bindTexture(icon.get(this, new ResourceLocation(
                    "textures/gui/sol_client_confusion_" + Utils.getTextureScale() + ".png")));
            Gui.drawModalRectWithCustomSizedTexture(0, 0, 0, 0, width, height, width, height);
        }

        super.render(info);
    }

    @Override
    protected Rectangle getDefaultBounds() {
        return new Rectangle(0, 0, width, height);
    }

}
