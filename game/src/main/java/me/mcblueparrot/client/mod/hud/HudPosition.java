package me.mcblueparrot.client.mod.hud;

import com.google.gson.annotations.Expose;

import me.mcblueparrot.client.util.Position;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

public class HudPosition {

    private static Minecraft mc = Minecraft.getMinecraft();
    @Expose
    public float x;
    @Expose
    public float y;

    public HudPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "Hud @ " + x + ", " + y;
    }

    public Position toAbsolute() {
        ScaledResolution res = new ScaledResolution(mc);
        return new Position((int) (res.getScaledWidth() * x), (int) (res.getScaledHeight() * y));
    }

    public static HudPosition fromAbsolute(Position absolute) {
        ScaledResolution res = new ScaledResolution(mc);
        return new HudPosition((float) (absolute.getX() / res.getScaledWidth_double()),
                (float) (absolute.getY() / res.getScaledHeight_double()));
    }

}
