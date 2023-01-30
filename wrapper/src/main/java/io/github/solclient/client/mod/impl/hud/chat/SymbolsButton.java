package io.github.solclient.client.mod.impl.hud.chat;

import org.lwjgl.input.Keyboard;

import io.github.solclient.client.chatextensions.ChatButton;
import io.github.solclient.client.mixin.client.ChatScreenAccessor;
import io.github.solclient.client.util.MinecraftUtils;
import io.github.solclient.client.util.data.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;

public class SymbolsButton implements ChatButton {

	private static String symbols = "☺☹♡♥◀▶▲▼←→↑↓«»©™‽☕✓✕⚐⚑⚠☆★✮✫☃☄";
	private static char[][] table;

	private static char[][] getSymbolTable() {
		if (table == null) {
			table = new char[6][6];
			int y = 0;
			int x = 0;
			for (char character : symbols.toCharArray()) {
				table[y][x] = character;
				x++;
				if (x > 5) {
					x = 0;
					y++;
				}
			}
			return table;
		}

		return table;
	}

	private final int priority;
	private final TextRenderer font;

	public SymbolsButton(ChatMod mod) {
		priority = mod.getIndex();
		font = MinecraftClient.getInstance().textRenderer;
	}

	@Override
	public int getPriority() {
		return priority;
	}

	@Override
	public int getPopupWidth() {
		return 77;
	}

	@Override
	public int getPopupHeight() {
		return getSymbolTable().length * 13 - 1;
	}

	@Override
	public int getWidth() {
		return 12;
	}

	@Override
	public String getText() {
		return "✮";
	}

	@Override
	public void render(int x, int y, boolean mouseDown, boolean wasMouseDown, boolean wasMouseClicked, int mouseX,
			int mouseY) {
		int originalX = x;
		for (char[] characters : getSymbolTable()) {
			x = originalX;
			for (char character : characters) {
				Rectangle characterBounds = new Rectangle(x, y, 12, 12);
				boolean selected = character != 0 && characterBounds.contains(mouseX, mouseY);
				MinecraftUtils.drawRectangle(characterBounds, selected ? Colour.WHITE_128 : Colour.BLACK_128);
				if (character != 0) {
					font.draw(character + "", x + (13 / 2) - (font.getCharWidth(character) / 2),
							characterBounds.getY() + (characterBounds.getHeight() / 2) - (font.fontHeight / 2),
							characterBounds.contains(mouseX, mouseY) ? 0 : -1);
				}

				if (selected && wasMouseClicked) {
					MinecraftUtils.playClickSound(false);
					((ChatScreenAccessor) MinecraftUtils.getChatScreen()).type(character, Keyboard.KEY_0);
				}
				x += 13;
			}
			y += 13;
		}
	}
}
