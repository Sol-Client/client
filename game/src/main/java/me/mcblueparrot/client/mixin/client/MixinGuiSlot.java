package me.mcblueparrot.client.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import net.minecraft.client.gui.GuiSlot;

@Mixin(GuiSlot.class)
public class MixinGuiSlot {

    @ModifyConstant(method = "handleMouseInput", constant = @Constant(intValue = 2, ordinal = 4))
    public int getScrollDivisor(int original) {
        return 1;
    }

}
