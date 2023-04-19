package io.github.solclient.client.mod.impl.scrollabletooltips.mixins;

import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.solclient.client.mod.impl.scrollabletooltips.ScrollableTooltipsMod;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.inventory.slot.Slot;

@Mixin(HandledScreen.class)
public class HandledScreenMixin {

	@Shadow
	private Slot focusedSlot;
	private Slot cachedSlot;

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/GlStateManager;popMatrix()V"))
	public void resetScrollOnSlotChange(int mouseX, int mouseY, float tickDelta, CallbackInfo ci) {
		if (ScrollableTooltipsMod.enabled && cachedSlot != focusedSlot) {
			cachedSlot = focusedSlot;
			ScrollableTooltipsMod.instance.resetScroll();
		}
	}
}