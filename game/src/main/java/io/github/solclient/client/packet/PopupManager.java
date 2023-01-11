package io.github.solclient.client.packet;

import java.util.*;

import org.lwjgl.input.Keyboard;

import io.github.solclient.client.GlobalConstants;
import io.github.solclient.client.event.EventHandler;
import io.github.solclient.client.event.impl.*;
import io.github.solclient.client.util.Utils;
import io.github.solclient.client.util.data.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.settings.*;
import net.minecraft.util.EnumChatFormatting;

public class PopupManager {

	private final Minecraft mc = Minecraft.getMinecraft();

	private final KeyBinding keyAcceptRequest = new KeyBinding(GlobalConstants.KEY_TRANSLATION_KEY + ".accept_request",
			Keyboard.KEY_Y, GlobalConstants.KEY_CATEGORY);
	private final KeyBinding keyDismissRequest = new KeyBinding(
			GlobalConstants.KEY_TRANSLATION_KEY + ".dismiss_request", Keyboard.KEY_N, GlobalConstants.KEY_CATEGORY);

	private final LinkedList<Popup> popups = new LinkedList<>();
	private final Map<UUID, Popup> popupsByHandle = new HashMap<>();
	private final Map<Popup, UUID> handlesByPopup = new HashMap<>();
	private Popup currentPopup;

	public PopupManager() {
		Utils.registerKeyBinding(keyAcceptRequest);
		Utils.registerKeyBinding(keyDismissRequest);
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
				String keys = EnumChatFormatting.GREEN + " [ "
						+ GameSettings.getKeyDisplayString(keyAcceptRequest.getKeyCode()) + " ] Accept"
						+ EnumChatFormatting.RED + "  [ "
						+ GameSettings.getKeyDisplayString(keyDismissRequest.getKeyCode()) + " ] Dismiss ";
				int width = Math.max(mc.fontRendererObj.getStringWidth(message),
						mc.fontRendererObj.getStringWidth(keys)) + 15;

				ScaledResolution resolution = new ScaledResolution(mc);

				Rectangle popupBounds = new Rectangle(resolution.getScaledWidth() / 2 - (width / 2), 10, width, 50);
				Utils.drawRectangle(popupBounds, new Colour(0, 0, 0, 100));
				Utils.drawRectangle(
						new Rectangle(popupBounds.getX(), popupBounds.getY() + popupBounds.getHeight() - 1, width, 2),
						Colour.BLACK);
				Utils.drawRectangle(new Rectangle(popupBounds.getX(), popupBounds.getY() + popupBounds.getHeight() - 1,
						(int) ((popupBounds.getWidth() / currentPopup.getTime()) * since), 2), Colour.BLUE);

				mc.fontRendererObj.drawString(message, popupBounds.getX() + (popupBounds.getWidth() / 2)
						- (mc.fontRendererObj.getStringWidth(message) / 2), 20, -1);

				mc.fontRendererObj.drawString(keys, popupBounds.getX() + (popupBounds.getWidth() / 2)
						- (mc.fontRendererObj.getStringWidth(keys) / 2), 40, -1);

				if (keyAcceptRequest.isPressed()) {
					mc.thePlayer.sendChatMessage(currentPopup.getCommand());
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
