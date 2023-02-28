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

package io.github.solclient.client.mod.impl.tweaks;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

import io.github.solclient.client.event.EventHandler;
import io.github.solclient.client.event.impl.FullscreenToggleEvent;
import io.github.solclient.client.event.impl.PreRenderTickEvent;
import io.github.solclient.client.mixin.client.MinecraftClientAccessor;
import io.github.solclient.client.util.data.Rectangle;
import lombok.RequiredArgsConstructor;
import net.minecraft.client.MinecraftClient;

@RequiredArgsConstructor
final class BorderlessFullscreen {

	private final TweaksMod mod;

	private final MinecraftClient mc = MinecraftClient.getInstance();
	private Rectangle previousBounds;
	private long fullscreenTime = -1;

	@EventHandler
	public void onFullscreenToggle(FullscreenToggleEvent event) {
		event.applyState = false;
		update(event.state);
	}

	@EventHandler
	public void onRender(PreRenderTickEvent event) {
		if (fullscreenTime != -1 && System.currentTimeMillis() - fullscreenTime >= 100) {
			fullscreenTime = -1;
			if (mc.focused) {
				mc.mouse.lockMouse();
			}
		}
	}

	public void update(boolean state) {
		try {
			System.setProperty("org.lwjgl.opengl.Window.undecorated", Boolean.toString(state));
			Display.setFullscreen(false);
			Display.setResizable(!state);

			if (state) {
				previousBounds = new Rectangle(Display.getX(), Display.getY(), mc.width, mc.height);

				Display.setDisplayMode(new DisplayMode(Display.getDesktopDisplayMode().getWidth(),
						Display.getDesktopDisplayMode().getHeight()));
				Display.setLocation(0, 0);
				((MinecraftClientAccessor) mc).resizeWindow(Display.getDesktopDisplayMode().getWidth(),
						Display.getDesktopDisplayMode().getHeight());
			} else {
				Display.setDisplayMode(new DisplayMode(previousBounds.getWidth(), previousBounds.getHeight()));
				Display.setLocation(previousBounds.getX(), previousBounds.getY());
				((MinecraftClientAccessor) mc).resizeWindow(previousBounds.getWidth(), previousBounds.getHeight());

				if (mc.focused) {
					mc.mouse.grabMouse();
					fullscreenTime = System.currentTimeMillis();
				}
			}
		} catch (LWJGLException error) {
			mod.getLogger().error("Could not toggle borderless fullscreen", error);
		}
	}

}
