package me.mcblueparrot.client.mixin.mod;

import java.util.List;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.mcblueparrot.client.mod.impl.BetterItemTooltipsMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;

public class MixinBetterItemTooltipsMod {

    @Mixin(GuiIngame.class)
    public static abstract class MixinGuiIngame {

        @Inject(method = "renderSelectedItem", at = @At("HEAD"), cancellable = true)
        public void drawExtraLines(ScaledResolution scaledRes, CallbackInfo callback) {
            if(BetterItemTooltipsMod.enabled) {
                callback.cancel();

                mc.mcProfiler.startSection("selectedItemName");

                if(remainingHighlightTicks > 0 && highlightingItemStack != null) {
                    List<String> lines = highlightingItemStack.getTooltip(mc.thePlayer, false);

                    int y = scaledRes.getScaledHeight() - 59;

                    int height = getFontRenderer().FONT_HEIGHT + 2;

                    y -= (height * (lines.size() - 1)) - 2;

                    if(!this.mc.playerController.shouldDrawHUD()) {
                        y += 14;
                    }

                    int opacity = (int)(this.remainingHighlightTicks * 256.0F / 10.0F);
                    opacity = Math.min(opacity, 255);

                    if(opacity > 0) {
                        GlStateManager.pushMatrix();
                        GlStateManager.enableBlend();
                        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                        for(String line : lines) {
                            int x = (scaledRes.getScaledWidth() - getFontRenderer().getStringWidth(line)) / 2;
                            getFontRenderer().drawStringWithShadow(line, x, y,
                                    16777215 + (opacity << 24));
                            y += height;
                        }
                        GlStateManager.disableBlend();
                        GlStateManager.popMatrix();
                    }
                }

                mc.mcProfiler.endSection();
            }
        }

        @Shadow @Final
        private Minecraft mc;

        @Shadow
        private int remainingHighlightTicks;

        @Shadow
        private ItemStack highlightingItemStack;

        @Shadow
        public abstract FontRenderer getFontRenderer();


    }

}
