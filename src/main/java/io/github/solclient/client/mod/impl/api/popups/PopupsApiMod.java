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

package io.github.solclient.client.mod.impl.api.popups;

import java.util.*;

import org.lwjgl.input.Keyboard;

import io.github.solclient.client.event.EventHandler;
import io.github.solclient.client.event.impl.*;
import io.github.solclient.client.mod.impl.StandardMod;
import io.github.solclient.client.mod.option.annotation.Option;
import io.github.solclient.client.util.MinecraftUtils;
import io.github.solclient.client.util.data.*;
import io.github.solclient.util.GlobalConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.*;
import net.minecraft.client.util.Window;
import net.minecraft.util.Formatting;

public final class PopupsApiMod extends StandardMod {

	public static PopupsApiMod instance;

	@Option
	private final KeyBinding keyAcceptRequest = new KeyBinding(GlobalConstants.KEY_TRANSLATION_KEY + ".accept_request",
			Keyboard.KEY_Y, GlobalConstants.KEY_CATEGORY);
	@Option
	private final KeyBinding keyDismissRequest = new KeyBinding(
			GlobalConstants.KEY_TRANSLATION_KEY + ".dismiss_request", Keyboard.KEY_N, GlobalConstants.KEY_CATEGORY);

	private final LinkedList<Popup> popups = new LinkedList<>();
	private final Map<UUID, Popup> popupsByHandle = new HashMap<>();
	private final Map<Popup, UUID> handlesByPopup = new HashMap<>();
	private Popup currentPopup;

	@Override
	public void init() {
		super.init();
		instance = this;
	}

	@EventHandler
	public void onRender(PostGameOverlayRenderEvent event) {
		if (event.type != GameOverlayElement.ALL)
			return;

		if (currentPopup != null) {
			long since = System.currentTimeMillis() - currentPopup.getStartTime();
			if (since > currentPopup.getTime()) {
				hidePopup();
			} else {
				String message = currentPopup.getText();
				String keys = Formatting.GREEN + " [ "
						+ GameOptions.getFormattedNameForKeyCode(keyAcceptRequest.getCode()) + " ] Accept"
						+ Formatting.RED + "  [ " + GameOptions.getFormattedNameForKeyCode(keyDismissRequest.getCode())
						+ " ] Dismiss ";
				int width = Math.max(mc.textRenderer.getStringWidth(message), mc.textRenderer.getStringWidth(keys))
						+ 15;

				Window window = new Window(mc);

				Rectangle popupBounds = new Rectangle(window.getWidth() / 2 - (width / 2), 10, width, 50);
				MinecraftUtils.drawRectangle(popupBounds, new Colour(0, 0, 0, 100));
				MinecraftUtils.drawRectangle(
						new Rectangle(popupBounds.getX(), popupBounds.getY() + popupBounds.getHeight() - 1, width, 2),
						Colour.BLACK);
				MinecraftUtils.drawRectangle(new Rectangle(popupBounds.getX(), popupBounds.getY() + popupBounds.getHeight() - 1,
						(int) ((popupBounds.getWidth() / currentPopup.getTime()) * since), 2), Colour.BLUE);

				mc.textRenderer.draw(message, popupBounds.getX() + (popupBounds.getWidth() / 2)
						- (mc.textRenderer.getStringWidth(message) / 2), 20, -1);

				mc.textRenderer.draw(keys,
						popupBounds.getX() + (popupBounds.getWidth() / 2) - (mc.textRenderer.getStringWidth(keys) / 2),
						40, -1);

				if (keyAcceptRequest.isPressed()) {
					mc.player.sendChatMessage(currentPopup.getCommand());
					hidePopup();
				} else if (keyDismissRequest.isPressed()) {
					hidePopup();
				}
			}
		}

		if (currentPopup == null && !popups.isEmpty()) {
			currentPopup = popups.pop();
			currentPopup.setTime();
		}
		keyAcceptRequest.isPressed();
		keyDismissRequest.isPressed();
	}

	private void hidePopup() {
		if (currentPopup != null) {
			UUID handle = handlesByPopup.remove(currentPopup);
			if (handle != null)
				popupsByHandle.remove(handle);
		}

		currentPopup = null;
	}

	public void add(Popup popup) {
		popups.add(popup);
	}

	public void add(Popup popup, UUID handle) {
		add(popup);

		if (handle != null) {
			popupsByHandle.put(handle, popup);
			handlesByPopup.put(popup, handle);
		}
	}

	public boolean remove(UUID handle) {
		Popup popup = popupsByHandle.remove(handle);
		if (popup == null)
			return false;
		if (currentPopup == popup)
			currentPopup = null;

		handlesByPopup.remove(popup);
		return popups.remove(popup);
	}

}
