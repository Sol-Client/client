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

package io.github.solclient.client.ui.screen;

import com.mojang.blaze3d.platform.GlStateManager;

import io.github.solclient.client.mod.impl.core.mixins.client.TitleScreenAccessor;
import io.github.solclient.client.ui.component.*;
import io.github.solclient.client.util.ActiveMainMenu;
import io.github.solclient.client.util.data.Colour;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.*;
import net.minecraft.client.render.*;

public abstract class PanoramaBackgroundScreen extends ComponentScreen {

	private final Screen mainMenu = ActiveMainMenu.getInstance();

	public PanoramaBackgroundScreen(Component root) {
		super(root);
		background = false;
	}

	@Override
	public void init(MinecraftClient mc, int width, int height) {
		super.init(mc, width, height);

		if (mc.world == null)
			mainMenu.init(mc, width, height);
	}

	@Override
	public void tick() {
		super.tick();
		mainMenu.tick();
	}

	protected void drawPanorama(int mouseX, int mouseY, float partialTicks) {
		TitleScreenAccessor accessor = (TitleScreenAccessor) mainMenu;

		client.getFramebuffer().unbind();

		GlStateManager.viewport(0, 0, 256, 256);
		accessor.drawPanorama(mouseX, mouseY, partialTicks);
		accessor.rotateAndBlurPanorama(partialTicks);
		accessor.rotateAndBlurPanorama(partialTicks);
		accessor.rotateAndBlurPanorama(partialTicks);
		accessor.rotateAndBlurPanorama(partialTicks);
		accessor.rotateAndBlurPanorama(partialTicks);
		accessor.rotateAndBlurPanorama(partialTicks);
		accessor.rotateAndBlurPanorama(partialTicks);
		client.getFramebuffer().bind(true);

		GlStateManager.viewport(0, 0, client.width, client.height);

		float uvBase = width > height ? 120.0F / width : 120.0F / height;
		float uBase = height * uvBase / 256.0F;
		float vBase = width * uvBase / 256.0F;

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();
		buffer.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
		buffer.vertex(0.0D, height, zOffset).texture((0.5F - uBase), (0.5F + vBase)).color(1.0F, 1.0F, 1.0F, 1.0F)
				.next();
		buffer.vertex(width, height, zOffset).texture(0.5F - uBase, 0.5F - vBase).color(1.0F, 1.0F, 1.0F, 1.0F).next();
		buffer.vertex(width, 0.0D, zOffset).texture(0.5F + uBase, 0.5F - vBase).color(1.0F, 1.0F, 1.0F, 1.0F).next();
		buffer.vertex(0.0D, 0.0D, zOffset).texture(0.5F + uBase, 0.5F + vBase).color(1.0F, 1.0F, 1.0F, 1.0F).next();
		tessellator.draw();

		fill(0, 0, width, height, new Colour(0, 0, 0, 100).getValue());

		GlStateManager.enableBlend();
		GlStateManager.color(1, 1, 1);
	}

}
