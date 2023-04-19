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

package io.github.solclient.client.mod.impl.tweaks.mixins;

import java.util.List;

import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.platform.GlStateManager;

import io.github.solclient.client.mod.impl.tweaks.TweaksMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.Window;
import net.minecraft.item.ItemStack;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {

	@Inject(method = "renderHeldItemName", at = @At("HEAD"), cancellable = true)
	public void drawExtraLines(Window window, CallbackInfo callback) {
		if (TweaksMod.enabled && TweaksMod.instance.betterTooltips) {
			callback.cancel();

			client.profiler.push("selectedItemName");

			if (heldItemTooltipFade > 0 && heldItem != null) {
				List<String> lines = heldItem.getTooltip(client.player, false);

				int y = window.getHeight() - 59;

				int height = getFontRenderer().fontHeight + 2;

				y -= (height * (lines.size() - 1)) - 2;

				if (!client.interactionManager.hasStatusBars())
					y += 14;

				int opacity = (int) (this.heldItemTooltipFade * 256.0F / 10.0F);
				opacity = Math.min(opacity, 255);

				if (opacity > 0) {
					GlStateManager.pushMatrix();
					GlStateManager.enableBlend();
					GlStateManager.blendFuncSeparate(770, 771, 1, 0);
					for (String line : lines) {
						int x = (window.getWidth() - getFontRenderer().getStringWidth(line)) / 2;
						getFontRenderer().drawWithShadow(line, x, y, 16777215 + (opacity << 24));
						y += height;
					}
					GlStateManager.disableBlend();
					GlStateManager.popMatrix();
				}
			}

			client.profiler.pop();
		}
	}

	@Final
	private @Shadow MinecraftClient client;

	@Shadow
	private int heldItemTooltipFade;

	@Shadow
	private ItemStack heldItem;

	@Shadow
	public abstract TextRenderer getFontRenderer();

}
