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

package io.github.solclient.client.ui;

import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.util.Identifier;

public class ReplayButton extends ButtonWidget {

	public static final Identifier ICON = new Identifier("replaymod", "logo_button.png");

	public ReplayButton(int buttonId, int x, int y) {
		super(buttonId, x, y, 20, 20, "");
	}

	@Override
	public void render(MinecraftClient mc, int mouseX, int mouseY) {
		super.render(mc, mouseX, mouseY);
		GlStateManager.color(1, 1, 1);
		mc.getTextureManager().bindTexture(ICON);
		drawTexture(x, y, 0, 0, width, height, width, height);
	}

}
