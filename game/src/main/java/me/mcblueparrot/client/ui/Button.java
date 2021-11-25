package me.mcblueparrot.client.ui;

import lombok.AllArgsConstructor;
import me.mcblueparrot.client.util.Colour;
import me.mcblueparrot.client.util.Rectangle;
import me.mcblueparrot.client.util.Utils;
import me.mcblueparrot.client.util.font.Font;
import me.mcblueparrot.client.util.font.SlickFontRenderer;

@AllArgsConstructor
public class Button {

    private Font font;
    private String text;
    private Rectangle bounds;
    private Colour colour;
    private Colour hoverColour;

    public void render(int mouseX, int mouseY) {
        Utils.drawRectangle(bounds,
                contains(mouseX, mouseY) ? hoverColour : colour);
        font.renderString(text, bounds.getX() + (bounds.getWidth() / 2) - (font.getWidth(text) / 2),
                bounds.getY() + (bounds.getHeight() / 2) + (font instanceof SlickFontRenderer ? 0 : 1) - 5, -1);
    }

    public boolean contains(int x, int y) {
        return bounds.contains(x, y);
    }

}
