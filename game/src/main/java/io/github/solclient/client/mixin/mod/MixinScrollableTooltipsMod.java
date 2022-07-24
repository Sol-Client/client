package io.github.solclient.client.mixin.mod;

import io.github.solclient.client.Client;
import io.github.solclient.client.mod.impl.ScrollableTooltipsMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

public class MixinScrollableTooltipsMod {

    @Mixin(GuiScreen.class)
    public static class MixinGuiScreen {
        @ModifyArgs(method = "renderToolTip", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiScreen;drawHoveringText(Ljava/util/List;II)V"))
        public void modifyTooltipPosition(Args args){

            ScrollableTooltipsMod mod = (ScrollableTooltipsMod) Client.INSTANCE.getModById(ScrollableTooltipsMod.Id);
            if(mod != null && mod.isEnabled()){

                if((Minecraft.getMinecraft().currentScreen instanceof GuiContainerCreative) &&
                        ((GuiContainerCreative)Minecraft.getMinecraft().currentScreen).getSelectedTabIndex() != CreativeTabs.tabInventory.getTabIndex()){
                    return;
                }

                mod.onRenderTooltip();
                args.set(1, (int)args.get(1) + mod.offsetX);
                args.set(2, (int)args.get(2) + mod.offsetY);
            }
        }

    }

    @Mixin(GuiContainer.class)
    public static class MixinGuiContainer {

        @Shadow
        private Slot theSlot;
        private Slot cachedSlot;

        @Inject(method = "drawScreen", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;popMatrix()V"))
        public void resetScrollOnSlotChange(int mouseX, int mouseY, float tickDelta, CallbackInfo ci){

            ScrollableTooltipsMod mod = (ScrollableTooltipsMod) Client.INSTANCE.getModById(ScrollableTooltipsMod.Id);
            if(mod != null && mod.isEnabled() && cachedSlot != theSlot){
                cachedSlot = theSlot;
                mod.resetScroll();
            }

        }
    }
}
