package me.mcblueparrot.client.mod.impl.hud;

import com.google.gson.annotations.Expose;

import me.mcblueparrot.client.CpsMonitor;
import me.mcblueparrot.client.mod.annotation.ConfigOption;
import me.mcblueparrot.client.mod.hud.SimpleHud;
import me.mcblueparrot.client.util.Colour;
import me.mcblueparrot.client.util.Position;

public class CpsHud extends SimpleHud {

    @Expose
    @ConfigOption("RMB")
    private boolean rmb;
    @Expose
    @ConfigOption("Separator Colour")
    private Colour separatorColour = new Colour(64, 64, 64);

    public CpsHud() {
        super("CPS", "cps", "Display your CPS (clicks per second).");
    }

    @Override
    public void render(Position position, boolean editMode) {
        super.render(position, editMode);
        if(rmb) {
            float x = position.getX() + (getBounds(position).getWidth() / 2F)
                    - ((font.getStringWidth(CpsMonitor.LMB.getCps() + " | " + CpsMonitor.RMB.getCps() + " CPS")) / 2F);
            x = font.drawString(CpsMonitor.LMB.getCps() + " ", x,
                    position.getY() + (getBounds(position).getHeight() / 2F) - (font.FONT_HEIGHT / 2F),
                    textColour.getValue(),
                    shadow);

            if(shadow) x--;
            x = font.drawString("|", x, position.getY() + textYOffset, separatorColour.getValue(), shadow);

            if(shadow) x--;

            x = font.drawString(" " + CpsMonitor.RMB.getCps() + " CPS", x, position.getY() + textYOffset,
                    textColour.getValue(), shadow);
        }
    }

    @Override
    public String getText(boolean editMode) {
        return rmb ? "" : CpsMonitor.LMB.getCps() + " CPS";
    }

}
