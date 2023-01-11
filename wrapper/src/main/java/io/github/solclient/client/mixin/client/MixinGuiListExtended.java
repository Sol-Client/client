package io.github.solclient.client.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;

@Mixin(GuiListExtended.class)
public abstract class MixinGuiListExtended extends GuiSlot {

	public MixinGuiListExtended(Minecraft mcIn, int width, int height, int topIn, int bottomIn, int slotHeightIn) {
		super(mcIn, width, height, topIn, bottomIn, slotHeightIn);
	}

	@Inject(method = "drawSlot", at = @At("HEAD"), cancellable = true)
	public void preDrawSlot(int entryID, int p_180791_2_, int p_180791_3_, int p_180791_4_, int mouseXIn, int mouseYIn,
			CallbackInfo callback) {
		if (p_180791_3_ + p_180791_4_ < top) {
			callback.cancel();
		} else if (p_180791_3_ > bottom) {
			callback.cancel();
		}
	}

}
