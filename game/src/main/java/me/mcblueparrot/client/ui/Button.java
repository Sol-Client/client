package me.mcblueparrot.client.ui;

import me.mcblueparrot.client.util.Colour;
import me.mcblueparrot.client.util.Rectangle;
import me.mcblueparrot.client.util.SlickFontRenderer;
import me.mcblueparrot.client.util.Utils;

public class Button {

    private SlickFontRenderer font = SlickFontRenderer.DEFAULT;
    private String text;
    private Rectangle bounds;
    private Colour colour;
    private Colour hoverColour;

    public Button(String text, Rectangle bounds, Colour colour, Colour hoverColour) {
        this.text = text;
        this.bounds = bounds;
        this.colour = colour.withAlpha(200);
        this.hoverColour = hoverColour.withAlpha(200);
    }

    public void render(int mouseX, int mouseY) {
        Utils.drawRectangle(bounds,
                contains(mouseX, mouseY) ? hoverColour : colour);
        font.drawString(text, bounds.getX() + (bounds.getWidth() / 2) - (font.getWidth(text) / 2),
                bounds.getY() + (bounds.getHeight() / 2) - 5, -1);
    }

    public boolean contains(int x, int y) {
        return bounds.contains(x, y);
    }

}
