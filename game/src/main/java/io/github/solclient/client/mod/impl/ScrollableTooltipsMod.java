package io.github.solclient.client.mod.impl;

import com.google.gson.annotations.Expose;
import io.github.solclient.client.mod.Mod;
import io.github.solclient.client.mod.ModCategory;
import io.github.solclient.client.mod.annotation.Option;
import io.github.solclient.client.mod.annotation.Slider;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Mouse;

public class ScrollableTooltipsMod extends Mod {

    public static String Id = "tooltip_scroll";
    public int offsetX;
    public int offsetY;


    @Expose
    @Option
    @Slider(min = 1.0f, max = 20f, step = 0.2F)
    public float scrollStep = 5f;

    @Override
    public String getId() {
        return Id;
    }

    @Override
    public ModCategory getCategory() {
        return ModCategory.UTILITY;
    }

    public void onRenderTooltip(){
        if(isEnabled()) {

            int i = Mouse.getDWheel();
            if (i != 0) {

                if (i < 0) {
                    onScroll(false);
                }

                if (i > 0) {
                    onScroll(true);
                }
            }
        }
    }

    public void onScroll(boolean reverse){

        if (GuiScreen.isShiftKeyDown()) {
            if(reverse){
                offsetX -= scrollStep;

            } else {
                offsetX += scrollStep;

            }

        } else {
            if (reverse) {
                offsetY -= scrollStep;

            } else {
                offsetY += scrollStep;

            }
        }
    }

    public void resetScroll(){
        offsetX = 0;
        offsetY = 0;
    }

}
