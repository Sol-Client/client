package me.mcblueparrot.client.ui;

import me.mcblueparrot.client.util.Colour;
import me.mcblueparrot.client.util.Rectangle;
import me.mcblueparrot.client.util.Utils;

public class Tickbox {

    private Rectangle bounds;
    private boolean value;

    public Tickbox(int x, int y, boolean value) {
        this.bounds = new Rectangle(x, y, 15, 15);
        this.value = value;
    }

    public void render(int mouseX, int mouseY, boolean hovered) {
        bounds.stroke(hovered ? new Colour(255, 220, 60) : new Colour(255, 180, 0));
        if(value) {
            Utils.drawRectangle(new Rectangle(bounds.getX() + 2, bounds.getY() + 2, bounds.getWidth() - 4,
                    bounds.getHeight() - 4),
                    hovered ? new Colour(255, 220, 60) : new Colour(255, 180, 0));
        }
    }

    public boolean contains(int x, int y) {
        return bounds.contains(x, y);
    }

}
