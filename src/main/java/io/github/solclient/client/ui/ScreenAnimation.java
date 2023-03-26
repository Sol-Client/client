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

import java.io.Closeable;

import org.lwjgl.nanovg.*;
import org.lwjgl.opengl.*;

import com.mojang.blaze3d.platform.GLX;

import io.github.solclient.client.lib.penner.easing.Sine;
import io.github.solclient.client.mod.impl.SolClientConfig;
import io.github.solclient.client.util.*;
import net.minecraft.client.MinecraftClient;

public class ScreenAnimation extends NanoVGManager implements Closeable {

	private static final int DURATION = 150;

	private int fbWidth, fbHeight;
	private NVGLUFramebuffer fb;
	private final long openTime = System.currentTimeMillis();

	private boolean isActive() {
		return SolClientConfig.instance.openAnimation && System.currentTimeMillis() - openTime < DURATION
				&& GLX.supportsFbo();
	}

	@Override
	public void close() {
		if (fb != null) {
			NanoVGGL2.nvgluDeleteFramebuffer(nvg, fb);
			fb = null;
		}
	}

	public boolean wrap(Runnable task) {
		if (!isActive()) {
			close();
			MinecraftUtils.withNvg(task, true);
			return false;
		}

		MinecraftClient mc = MinecraftClient.getInstance();

		if (fbWidth != mc.width || fbHeight != mc.height)
			close();

		if (fb == null) {
			fbWidth = mc.width;
			fbHeight = mc.height;
			fb = NanoVGGL2.nvgluCreateFramebuffer(nvg, mc.width, mc.height, 0);
		}

		NanoVGGL2.nvgluBindFramebuffer(nvg, fb);
		GL11.glViewport(0, 0, mc.width, mc.height);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
		MinecraftUtils.withNvg(task, true);
		mc.getFramebuffer().bind(true);

		// render the frame with transformations
		MinecraftUtils.withNvg(() -> {
			float progress = Sine.easeOut(Math.min(System.currentTimeMillis() - openTime, DURATION), 0, 1, DURATION);
			NanoVG.nvgGlobalAlpha(nvg, progress);
			progress = 0.85F + (progress * 0.15F);
			NanoVG.nvgTranslate(nvg, (mc.width / 2F) * (1 - progress), (mc.height / 2F) * (1 - progress));
			NanoVG.nvgScale(nvg, progress, progress);

			NanoVG.nvgBeginPath(nvg);
			NanoVG.nvgRect(nvg, 0, 0, mc.width, mc.height);
			NanoVG.nvgFillPaint(nvg, MinecraftUtils.nvgTexturePaint(nvg, fb.image(), 0, 0, mc.width, mc.height, 0));
			NanoVG.nvgFill(nvg);
		}, false);
		return true;
	}

}
