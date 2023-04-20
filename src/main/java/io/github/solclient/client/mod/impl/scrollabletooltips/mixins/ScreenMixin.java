/*
 * Sol Client - an open source Minecraft client
 * Copyright (C) 2021-2023  TheKodeToad and Contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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
