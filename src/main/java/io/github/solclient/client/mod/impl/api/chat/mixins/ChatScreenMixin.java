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

package io.github.solclient.client.mod.impl.api.chat.mixins;

import java.util.List;

import org.lwjgl.input.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

import io.github.solclient.client.extension.ChatScreenExtension;
import io.github.solclient.client.mod.impl.api.chat.*;
import io.github.solclient.client.util.MinecraftUtils;
import io.github.solclient.client.util.data.*;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.*;

@Mixin(ChatScreen.class)
public abstract class ChatScreenMixin extends Screen implements ChatScreenExtension {

	private ChatButton selectedButton;
	private boolean wasMouseDown;

	@Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ChatScreen;fill(IIIII)V"))
	public void addChatButtons(int left, int top, int right, int bottom, int color, int mouseX, int mouseY,
			float partialTicks) {
		boolean mouseDown = Mouse.isButtonDown(0);

		List<ChatButton> buttons = ChatApiMod.instance.getButtons();

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
