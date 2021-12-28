package me.mcblueparrot.client.api;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

import org.lwjgl.input.Keyboard;

import me.mcblueparrot.client.Client;
import me.mcblueparrot.client.event.EventHandler;
import me.mcblueparrot.client.event.impl.GameOverlayElement;
import me.mcblueparrot.client.event.impl.PostGameOverlayRenderEvent;
import me.mcblueparrot.client.util.Utils;
import me.mcblueparrot.client.util.data.Colour;
import me.mcblueparrot.client.util.data.Rectangle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.EnumChatFormatting;

public class PopupManager {

	private Minecraft mc = Minecraft.getMinecraft();

	private KeyBinding keyAcceptRequest = new KeyBinding("Accept Request", Keyboard.KEY_Y, "Sol Client");
	private KeyBinding keyDismissRequest = new KeyBinding("Dismiss Request", Keyboard.KEY_N, "Sol Client");

	private Deque<Popup> popups = new ArrayDeque<>();
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
