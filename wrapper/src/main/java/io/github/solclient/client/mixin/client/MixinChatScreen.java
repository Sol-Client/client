package io.github.solclient.client.mixin.client;

import java.util.List;

import org.lwjgl.input.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.*;

import io.github.solclient.client.Client;
import io.github.solclient.client.chatextensions.ChatButton;
import io.github.solclient.client.extension.ChatScreenExtension;
import io.github.solclient.client.util.MinecraftUtils;
import io.github.solclient.client.util.data.*;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.*;

@Mixin(ChatScreen.class)
public abstract class MixinChatScreen extends Screen implements ChatScreenExtension {

	private ChatButton selectedButton;
	private boolean wasMouseDown;

	@Override
	@Invoker("keyPressed")
	public abstract void type(char character, int code);

	@Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ChatScreen;fill(IIIII)V"))
	public void addChatButtons(int left, int top, int right, int bottom, int color, int mouseX, int mouseY,
			float partialTicks) {
		boolean mouseDown = Mouse.isButtonDown(0);

		List<ChatButton> buttons = Client.INSTANCE.getChatExtensions().getButtons();

		for (ChatButton button : buttons) {
			int start = right - button.getWidth();
			Rectangle buttonBounds = new Rectangle(start, height - 14, button.getWidth(), 12);

			MinecraftUtils.drawRectangle(buttonBounds,
					buttonBounds.contains(mouseX, mouseY) ? Colour.WHITE_128 : Colour.BLACK_128);

			textRenderer.draw(button.getText(),
					start + (button.getWidth() / 2) - (textRenderer.getStringWidth(button.getText()) / 2),
					this.height - 8 - (textRenderer.fontHeight / 2), buttonBounds.contains(mouseX, mouseY) ? 0 : -1);

			if (mouseDown && !wasMouseDown && buttonBounds.contains(mouseX, mouseY)) {
				if (selectedButton == button) {
					MinecraftUtils.playClickSound(false);
					selectedButton = null;
				} else {
					MinecraftUtils.playClickSound(false);
					selectedButton = button;
				}
			}

			if (selectedButton == button) {
				button.render(right - button.getPopupWidth(), this.height - 15 - button.getPopupHeight(), mouseDown,
						wasMouseDown, mouseDown && !wasMouseDown, mouseX, mouseY);
			}

			right = start - 1;
		}

		DrawableHelper.fill(left, top, right, bottom, color);

		wasMouseDown = mouseDown;
	}

	@Override
	public ChatButton getSelectedChatButton() {
		return selectedButton;
	}

	@Override
	public void setSelectedChatButton(ChatButton button) {
		selectedButton = button;
	}

}
