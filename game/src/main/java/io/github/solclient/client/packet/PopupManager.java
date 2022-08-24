package io.github.solclient.client.packet;

import java.util.ArrayDeque;
import java.util.Deque;

import io.github.solclient.client.Constants;
import io.github.solclient.client.event.EventHandler;
import io.github.solclient.client.event.impl.hud.PostHudRenderEvent;
import io.github.solclient.client.platform.mc.MinecraftClient;
import io.github.solclient.client.platform.mc.Window;
import io.github.solclient.client.platform.mc.option.KeyBinding;
import io.github.solclient.client.platform.mc.text.TextFormatting;
import io.github.solclient.client.platform.mc.util.Input;
import io.github.solclient.client.platform.mc.util.MinecraftUtil;
import io.github.solclient.client.util.data.Colour;
import io.github.solclient.client.util.data.Rectangle;

public class PopupManager {

	private final MinecraftClient mc = MinecraftClient.getInstance();

	private final KeyBinding acceptRequestKey = KeyBinding.create(Constants.KEY_TRANSLATION_KEY + ".accept_request", Input.Y, Constants.KEY_CATEGORY);
	private final KeyBinding dismissRequestKey = KeyBinding.create(Constants.KEY_TRANSLATION_KEY + ".dismiss_request", Input.N, Constants.KEY_CATEGORY);

	private final Deque<Popup> popups = new ArrayDeque<>();
	private Popup currentPopup;

	public PopupManager() {
		mc.getOptions().addKey(acceptRequestKey);
		mc.getOptions().addKey(dismissRequestKey);
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
				String keys = TextFormatting.GREEN + " [ " + Input.getKeyName(acceptRequestKey.getKeyCode())
						+ " ] Accept" + TextFormatting.RED + "  [ "
						+ Input.getKeyName(dismissRequestKey.getKeyCode()) + " ] Dismiss ";
				int width = Math.max(mc.getFont().getWidth(message), mc.getFont().getWidth(keys)) + 15;

				Window window = mc.getWindow();

				Rectangle popupBounds = new Rectangle(window.getScaledWidth() / 2 - (width / 2), 10, width, 50);
				popupBounds.fill(new Colour(0, 0, 0, 100));
				new Rectangle(popupBounds.getX(), popupBounds.getY() + popupBounds.getHeight() - 1, width, 2)
						.fill(Colour.BLACK);
				new Rectangle(popupBounds.getX(), popupBounds.getY() + popupBounds.getHeight() - 1,
						(int) ((popupBounds.getWidth() / 10000F) * (since)), 2).fill(Colour.BLUE);

				mc.getFont().render(message,
						popupBounds.getX() + (popupBounds.getWidth() / 2) - (mc.getFont().getWidth(message) / 2), 20,
						-1);

				mc.getFont().render(keys,
						popupBounds.getX() + (popupBounds.getWidth() / 2) - (mc.getFont().getWidth(keys) / 2), 40,
						-1);

				if(acceptRequestKey.consumePress()) {
					mc.getPlayer().chat(currentPopup.getCommand());
					currentPopup = null;
				}
				else if(dismissRequestKey.consumePress()) {
					currentPopup = null;
				}
			}
		}

		if(currentPopup == null && !popups.isEmpty()) {
			currentPopup = popups.pop();
			currentPopup.setTime();
		}
		acceptRequestKey.clearPresses();
		dismissRequestKey.clearPresses();
	}

	public void add(Popup popup) {
		popups.add(popup);
	}

}
