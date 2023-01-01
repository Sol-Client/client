package io.github.solclient.client.mixin.mod;

import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import io.github.solclient.client.mod.impl.ScrollableTooltipsMod;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.*;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.inventory.Slot;

public class MixinScrollableTooltipsMod {

	@Mixin(GuiScreen.class)
	public static class MixinGuiScreen {

		@ModifyArgs(method = "renderToolTip", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiScreen;drawHoveringText(Ljava/util/List;II)V"))
		public void modifyTooltipPosition(Args args) {
			if (!((Object) this instanceof GuiContainer)) {
				return;
			}

			if (!ScrollableTooltipsMod.enabled) {
				return;
			}

			if (((Object) this instanceof GuiContainerCreative)) {
				GuiContainerCreative creative = (GuiContainerCreative) (Object) this;

				if (creative.getSelectedTabIndex() != CreativeTabs.tabInventory.getTabIndex()) {
					return;
				}
			}

			ScrollableTooltipsMod instance = ScrollableTooltipsMod.instance;
			instance.onRenderTooltip();

			args.set(1, (int) args.get(1) + instance.offsetX);
			args.set(2, (int) args.get(2) + instance.offsetY);
		}

	}

	@Mixin(GuiContainer.class)
	public static class MixinGuiContainer {

		@Shadow
		private Slot theSlot;
		private Slot cachedSlot;

		@Inject(method = "drawScreen", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;popMatrix()V"))
		public void resetScrollOnSlotChange(int mouseX, int mouseY, float tickDelta, CallbackInfo ci) {
			if (ScrollableTooltipsMod.enabled && cachedSlot != theSlot) {
				cachedSlot = theSlot;
				ScrollableTooltipsMod.instance.resetScroll();
			}
		}
	}
}
