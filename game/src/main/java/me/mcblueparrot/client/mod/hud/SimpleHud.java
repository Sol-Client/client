package me.mcblueparrot.client.mod.hud;

import com.google.gson.annotations.Expose;

import me.mcblueparrot.client.mod.annotation.ConfigOption;
import me.mcblueparrot.client.util.Colour;
import me.mcblueparrot.client.util.Position;
import me.mcblueparrot.client.util.Rectangle;

public abstract class SimpleHud extends Hud {

    @Expose
    @ConfigOption("Background")
    private boolean background = true;
    @Expose
    @ConfigOption("Background Colour")
    private Colour backgroundColour = new Colour(0, 0, 0, 100);
    @Expose
    @ConfigOption("Border")
    private boolean border = false;
    @Expose
    @ConfigOption("Border Colour")
    private Colour borderColour = Colour.BLACK;
    @Expose
    @ConfigOption("Text Colour")
    protected Colour textColour = Colour.WHITE;
    @Expose
    @ConfigOption("Text Shadow")
    protected boolean shadow = true;

    public SimpleHud(String name, String id, String description) {
        super(name, id, description);
    }

    @Override
    public Rectangle getBounds(Position position) {
        return new Rectangle(position.getX(), position.getY(), 53, 16);
    }

    @Override
    public void render(Position position, boolean editMode) {
        String text = getText(editMode);
        if(text != null) {
            if(background) {
                getBounds(position).fill(backgroundColour);
            }
            if(border) {
                getBounds(position).stroke(borderColour);
            }
            font.drawString(text,
                    position.getX() + (getBounds(position).getWidth() / 2F) - (font.getStringWidth(text) / 2F),
                    position.getY() + 4, textColour.getValue(), shadow);
        }
    }

    public abstract String getText(boolean editMode);

}
