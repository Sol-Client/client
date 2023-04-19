package io.github.solclient.client.mod.impl.scrollabletooltips.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import io.github.solclient.client.mod.impl.scrollabletooltips.ScrollableTooltipsMod;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.*;
import net.minecraft.item.itemgroup.ItemGroup;

@Mixin(Screen.class)
public class ScreenMixin {

	@ModifyArgs(method = "renderTooltip(Lnet/minecraft/item/ItemStack;II)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;renderTooltip(Ljava/util/List;II)V"))
	public void modifyTooltipPosition(Args args) {
		if (!((Object) this instanceof HandledScreen))
			return;

		if (!ScrollableTooltipsMod.enabled)
			return;

		if (((Object) this instanceof CreativeInventoryScreen)) {
			CreativeInventoryScreen creative = (CreativeInventoryScreen) (Object) this;

			if (creative.getSelectedTab() != ItemGroup.INVENTORY.getIndex())
				return;
		}

		ScrollableTooltipsMod instance = ScrollableTooltipsMod.instance;
		instance.onRenderTooltip();

		args.set(1, (int) args.get(1) + instance.offsetX);
		args.set(2, (int) args.get(2) + instance.offsetY);
	}

}
