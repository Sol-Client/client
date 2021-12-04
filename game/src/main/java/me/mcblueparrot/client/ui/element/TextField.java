package me.mcblueparrot.client.ui.element;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.mcblueparrot.client.util.Utils;
import me.mcblueparrot.client.util.font.Font;
import me.mcblueparrot.client.util.font.SlickFontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;

@RequiredArgsConstructor
public class TextField {

	@Setter
	private Font font;
	private final String placeholder;
	@Getter
	@Setter
	private String text = "";
	private final int x, y, width;
	private final boolean underline;
	private final Runnable onChange;

	public void render(int mouseX, int mouseY) {
		font.renderString(text.isEmpty() ? placeholder : text, x + 2 + (text.isEmpty() ? 2 : 0),
				y + 2 + (font instanceof SlickFontRenderer ? 0 : 1), text.isEmpty() ? 0xFF666666 : -1);

		Gui.drawRect((int) (x + 2 + font.getWidth(text)), y + 2,
				(int) (x + 3 + font.getWidth(text)), y + 12, -1);

		if(underline) {
			Utils.drawHorizontalLine(x, x + width, y + 15, -1);
		}
	}

	public boolean keyPressed(int key, char character) {
		if(character > 31 && character != '§') {
			text += character;

			onChange.run();

			return true;
		}
		else if(character == 8 && !text.isEmpty()) {
			if(GuiScreen.isCtrlKeyDown()) {
				text = "";
			}
			else {
				text = text.substring(0, text.length() - 1);
			}

			onChange.run();

			return true;
		}

		return false;
	}

}
