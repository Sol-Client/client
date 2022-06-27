package io.github.solclient.client.packet;

import java.util.ArrayDeque;
import java.util.Deque;

import org.lwjgl.input.Keyboard;

import io.github.solclient.abstraction.mc.MinecraftClient;
import io.github.solclient.abstraction.mc.Window;
import io.github.solclient.abstraction.mc.option.KeyBinding;
import io.github.solclient.abstraction.mc.text.TextColour;
import io.github.solclient.abstraction.mc.text.TextFormatting;
import io.github.solclient.abstraction.mc.util.MinecraftUtil;
import io.github.solclient.client.Client;
import io.github.solclient.client.event.EventHandler;
import io.github.solclient.client.event.impl.hud.PostHudRenderEvent;
import io.github.solclient.client.util.Utils;
import io.github.solclient.client.util.data.Colour;
import io.github.solclient.client.util.data.Rectangle;

public class PopupManager {

	private final MinecraftClient mc = MinecraftClient.getInstance();

	private final KeyBinding keyAcceptRequest = KeyBinding.create(Client.KEY_TRANSLATION_KEY + ".accept_request", Keyboard.KEY_Y, Client.KEY_CATEGORY);
	private final KeyBinding keyDismissRequest = KeyBinding.create(Client.KEY_TRANSLATION_KEY + ".dismiss_request", Keyboard.KEY_N, Client.KEY_CATEGORY);

	private final Deque<Popup> popups = new ArrayDeque<>();
	private Popup currentPopup;

	public PopupManager() {
		mc.getOptions().addKey(keyAcceptRequest);
		mc.getOptions().addKey(keyDismissRequest);
	}

	@EventHandler
	public void onRender(PostHudRenderEvent event) {
		if(currentPopup != null) {
			long since = System.currentTimeMillis() - currentPopup.getTime();
			if(since > 10000) {
				currentPopup = null;
			}
			else {
				String message = currentPopup.getText();
				String keys = TextFormatting.GREEN + " [ " + MinecraftUtil.getKeyName(keyAcceptRequest.getKeyCode())
						+ " ] Accept" + TextFormatting.RED + "  [ "
						+ MinecraftUtil.getKeyName(keyDismissRequest.getKeyCode()) + " ] Dismiss ";
				int width = Math.max(mc.getFont().getWidth(message), mc.getFont().getWidth(keys)) + 15;

				Window window = mc.getWindow();

				Rectangle popupBounds = new Rectangle(window.getScaledWidth() / 2 - (width / 2), 10, width, 50);
				Utils.drawRectangle(popupBounds, new Colour(0, 0, 0, 100));
				Utils.drawRectangle(new Rectangle(popupBounds.getX(), popupBounds.getY() + popupBounds.getHeight() - 1, width, 2), Colour.BLACK);
				Utils.drawRectangle(new Rectangle(popupBounds.getX(),
						popupBounds.getY() + popupBounds.getHeight() - 1,
						(int) ((popupBounds.getWidth() / 10000F) * (since)), 2), Colour.BLUE);

				mc.getFont().render(message,
						popupBounds.getX() + (popupBounds.getWidth() / 2) - (mc.getFont().getWidth(message) / 2), 20,
						-1);

				mc.getFont().render(keys,
						popupBounds.getX() + (popupBounds.getWidth() / 2) - (mc.getFont().getWidth(keys) / 2), 40,
						-1);

				if(keyAcceptRequest.consumePress()) {
					mc.getPlayer().chat(currentPopup.getCommand());
					currentPopup = null;
				}
				else if(keyDismissRequest.consumePress()) {
					currentPopup = null;
				}
			}
		}

		if(currentPopup == null && !popups.isEmpty()) {
			currentPopup = popups.pop();
			currentPopup.setTime();
		}
		keyAcceptRequest.clearPresses();
		keyDismissRequest.clearPresses();
	}

	public void add(Popup popup) {
		popups.add(popup);
	}

}
