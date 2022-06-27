/*
 * Includes modified decompiled Replay Mod class files.
 *
 * License for Replay Mod:
 *
 *     Copyright (C) <year>  <name of author>
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package io.github.solclient.client.mod.impl.replay.fix;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.replaymod.core.ReplayMod;
import com.replaymod.core.events.PostRenderCallback;
import com.replaymod.core.events.PreRenderCallback;
import com.replaymod.lib.de.johni0702.minecraft.gui.versions.MatrixStack;
import com.replaymod.lib.de.johni0702.minecraft.gui.versions.callbacks.InitScreenCallback;
import com.replaymod.lib.de.johni0702.minecraft.gui.versions.callbacks.OpenGuiScreenCallback;
import com.replaymod.lib.de.johni0702.minecraft.gui.versions.callbacks.PostRenderScreenCallback;
import com.replaymod.lib.de.johni0702.minecraft.gui.versions.callbacks.PreTickCallback;
import com.replaymod.lib.de.johni0702.minecraft.gui.versions.callbacks.RenderHudCallback;
import com.replaymod.replay.events.RenderSpectatorCrosshairCallback;

import io.github.solclient.client.event.EventHandler;
import io.github.solclient.client.event.impl.GameOverlayElement;
import io.github.solclient.client.event.impl.OpenGuiEvent;
import io.github.solclient.client.event.impl.PostGuiInitEvent;
import io.github.solclient.client.event.impl.PostGuiRenderEvent;
import io.github.solclient.client.event.impl.PostRenderTickEvent;
import io.github.solclient.client.event.impl.PreGameOverlayRenderEvent;
import io.github.solclient.client.event.impl.PreGuiInitEvent;
import io.github.solclient.client.event.impl.PreRenderTickEvent;
import io.github.solclient.client.event.impl.game.PreTickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;

public class SCReplayModBackend extends SCEventRegistrations {

	private final ReplayMod mod = new ReplayMod(this);

	public void init() {
		new File(Minecraft.getMinecraft().mcDataDir, "config").mkdirs();
		register();
		try {
			Method initModulesMethod = mod.getClass().getDeclaredMethod("initModules");
			initModulesMethod.setAccessible(true);
			initModulesMethod.invoke(mod);
		}
		catch(NoSuchMethodException | IllegalAccessException error) {
			throw new Error(error);
		}
		catch (InvocationTargetException error) {
			if(error.getCause() instanceof Error) {
				throw (Error) error.getCause();
			}
			else if(error.getCause() instanceof RuntimeException) {
				throw (RuntimeException) error.getCause();
			}
		}
		KeyBinding.resetKeyBindingArrayAndHash();
	}

	public String getVersion() {
		return "2.6.1";
	}

	public String getMinecraftVersion() {
		return "1.8.9";
	}

	public boolean isModLoaded(String id) {
		return false;
	}

	@EventHandler
	public void onRenderHud(PreGameOverlayRenderEvent event) {
		if(!event.cancelled) {
			if(event.type == GameOverlayElement.ALL) {
				RenderHudCallback.EVENT.invoker().renderHud(new MatrixStack(), event.partialTicks);
			}
			else if(event.type == GameOverlayElement.CROSSHAIRS) {
				event.cancelled =
						RenderSpectatorCrosshairCallback.EVENT.invoker().shouldRenderSpectatorCrosshair() == Boolean.FALSE;
			}
		}
	}

	@EventHandler
	public void preGuiInit(PreGuiInitEvent event) {
		InitScreenCallback.Pre.EVENT.invoker().preInitScreen(event.screen);
	}

	@EventHandler
	public void onGuiInit(PostGuiInitEvent event) {
		InitScreenCallback.EVENT.invoker().initScreen(event.screen, event.buttonList);
	}

	@EventHandler
	public void onGuiOpen(OpenGuiEvent event) {
		OpenGuiScreenCallback.EVENT.invoker().openGuiScreen(event.screen);
	}

	@EventHandler
	public void onGuiRender(PostGuiRenderEvent event) {
		PostRenderScreenCallback.EVENT.invoker().postRenderScreen(new MatrixStack(), event.partialTicks);
	}

	@EventHandler
	public void tickOverlay(PreTickEvent event) {
		PreTickCallback.EVENT.invoker().preTick();
	}

	@EventHandler
	public void onPreRender(PreRenderTickEvent event) {
		PreRenderCallback.EVENT.invoker().preRender();
	}

	@EventHandler
	public void onPostRender(PostRenderTickEvent event) {
		PostRenderCallback.EVENT.invoker().postRender();
	}

}
