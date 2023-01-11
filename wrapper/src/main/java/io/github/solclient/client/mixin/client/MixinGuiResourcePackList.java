package io.github.solclient.client.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;

@Mixin(GuiResourcePackList.class)
public abstract class MixinGuiResourcePackList extends GuiSlot {

	public MixinGuiResourcePackList(Minecraft mcIn, int width, int height, int topIn, int bottomIn, int slotHeightIn) {
		super(mcIn, width, height, topIn, bottomIn, slotHeightIn);
	}

	@Inject(method = "<init>", at = @At("RETURN"))
	public void overrideTop(CallbackInfo callback) {
		top += 16;
		height -= 16;
		setHasListHeader(false, 0);
	}

}
