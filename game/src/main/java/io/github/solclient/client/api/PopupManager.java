package io.github.solclient.client.api;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

import org.lwjgl.input.Keyboard;

import io.github.solclient.client.Client;
import io.github.solclient.client.GlobalConstants;
import io.github.solclient.client.event.EventHandler;
import io.github.solclient.client.event.impl.GameOverlayElement;
import io.github.solclient.client.event.impl.PostGameOverlayRenderEvent;
import io.github.solclient.client.util.Utils;
import io.github.solclient.client.util.data.Colour;
import io.github.solclient.client.util.data.Rectangle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.EnumChatFormatting;

public class PopupManager {

	private final Minecraft mc = Minecraft.getMinecraft();

	private final KeyBinding keyAcceptRequest = new KeyBinding(GlobalConstants.KEY_TRANSLATION_KEY + ".accept_request", Keyboard.KEY_Y, GlobalConstants.KEY_CATEGORY);
	private final KeyBinding keyDismissRequest = new KeyBinding(GlobalConstants.KEY_TRANSLATION_KEY + ".dismiss_request", Keyboard.KEY_N, GlobalConstants.KEY_CATEGORY);

	private final Deque<Popup> popups = new ArrayDeque<>();
	private Popup currentPopup;

	public PopupManager() {
		Client.INSTANCE.registerKeyBinding(keyAcceptRequest);
		Client.INSTANCE.registerKeyBinding(keyDismissRequest);
	}

	@EventHandler
	public void onRender(PostGameOverlayRenderEvent event) {
		if(event.type != GameOverlayElement.ALL) return;

		if(currentPopup != null) {
			long since = System.currentTimeMillis() - currentPopup.getTime();
			if(since > 10000) {
				currentPopup = null;
			}
			else {
				String message = currentPopup.getText();
				String keys = EnumChatFormatting.GREEN + " [ " + GameSettings.getKeyDisplayString(keyAcceptRequest.getKeyCode()) + " ] Accept" +
						EnumChatFormatting.RED + "  [ " + GameSettings.getKeyDisplayString(keyDismissRequest.getKeyCode()) + " ] Dismiss ";
				int width = Math.max(mc.fontRendererObj.getStringWidth(message), mc.fontRendererObj.getStringWidth(keys)) + 15;

				ScaledResolution resolution = new ScaledResolution(mc);

				Rectangle popupBounds = new Rectangle(resolution.getScaledWidth() / 2 - (width / 2), 10, width, 50);
				Utils.drawRectangle(popupBounds, new Colour(0, 0, 0, 100));
				Utils.drawRectangle(new Rectangle(popupBounds.getX(), popupBounds.getY() + popupBounds.getHeight() - 1, width, 2), Colour.BLACK);
				Utils.drawRectangle(new Rectangle(popupBounds.getX(),
						popupBounds.getY() + popupBounds.getHeight() - 1,
						(int) ((popupBounds.getWidth() / 10000F) * (since)), 2), Colour.BLUE);

				mc.fontRendererObj.drawString(message,
						popupBounds.getX() + (popupBounds.getWidth() / 2) - (mc.fontRendererObj.getStringWidth(message) / 2), 20,
						-1);

				mc.fontRendererObj.drawString(keys,
						popupBounds.getX() + (popupBounds.getWidth() / 2) - (mc.fontRendererObj.getStringWidth(keys) / 2), 40,
						-1);

				if(keyAcceptRequest.isPressed()) {
					mc.thePlayer.sendChatMessage(currentPopup.getCommand());
					currentPopup = null;
				}
				else if(keyDismissRequest.isPressed()) {
					currentPopup = null;
				}
			}
		}

		if(currentPopup == null && !popups.isEmpty()) {
			currentPopup = popups.pop();
			currentPopup.setTime();
		}
		keyAcceptRequest.isPressed();
		keyDismissRequest.isPressed();
	}

	public void add(Popup popup) {
		popups.add(popup);
	}

}
