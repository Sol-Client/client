package me.mcblueparrot.client.mod.impl.hud;

import java.text.DecimalFormat;

import com.google.gson.annotations.Expose;

import me.mcblueparrot.client.mod.annotation.ConfigOption;
import me.mcblueparrot.client.mod.hud.Hud;
import me.mcblueparrot.client.util.Colour;
import me.mcblueparrot.client.util.Position;
import me.mcblueparrot.client.util.Rectangle;
import net.minecraft.util.MathHelper;

public class PositionHud extends Hud {

    private String editString = "XYZ: 000.000 / 00.000 / 000.000";
    private int editWidth = mc.fontRendererObj.getStringWidth(editString);
    private int editHeight = mc.fontRendererObj.FONT_HEIGHT;
    private static final DecimalFormat FORMAT = new DecimalFormat("0.000");
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
    @ConfigOption("Axis Label Colour")
    private Colour axisLabelColour = new Colour(0, 150, 255);
    @Expose
    @ConfigOption("Axis Value Colour")
    private Colour axisValueColour = Colour.WHITE;
    @Expose
    @ConfigOption("Cardinal Direction")
    private boolean cardinalDirection = true;
    @Expose
    @ConfigOption("Cardinal Direction Colour")
    private Colour cardinalDirectionColour = new Colour(0, 150, 255);
    @Expose
    @ConfigOption("Axis Direction")
    private boolean axisDirection = true;
    @Expose
    @ConfigOption("Axis Direction Colour")
    private Colour axisDirectionColour = new Colour(0, 150, 255);
    @Expose
    @ConfigOption("Text Shadow")
    private boolean shadow = true;

    public PositionHud() {
        super("Coordinates", "position", "Display your coordinates.");
    }

    @Override
    public Rectangle getBounds(Position position) {
        return new Rectangle(position.getX(), position.getY(), 82, 4 + font.FONT_HEIGHT + 2 + font.FONT_HEIGHT + 2 + font.FONT_HEIGHT + 2);
    }

    @Override
    public void render(Position position, boolean editMode) {
        int subtract = 0;
        if(shadow) {
            subtract++;
        }

        if(background) {
            getBounds(position).fill(backgroundColour);
        }

        if(border) {
            getBounds(position).stroke(borderColour);
        }

        double x, y, z, yaw;
        if(editMode) {
            x = 0;
            y = 0;
            z = 0;
            yaw = -90;
        }
        else {
            x = mc.thePlayer.posX;
            y = mc.thePlayer.posY;
            z = mc.thePlayer.posZ;
            yaw = mc.thePlayer.rotationYaw;
        }
        int width = 80;
        int cardinalDirectionIndex = MathHelper.floor_double(
                ((MathHelper.wrapAngleTo180_double(yaw) + 180D + 22.5D) % 360D) / 45D);
        String[] cardinalDirections = {
                "N",
                "NE",
                "E",
                "SE",
                "S",
                "SW",
                "W",
                "NW"
        };
        String xDirection = null;
        String zDirection = null;
        switch(cardinalDirectionIndex) {
            case 0:
                zDirection = "--";
                break;
            case 1:
                zDirection = "-";
                xDirection = "+";
                break;
            case 2:
                xDirection = "++";
                break;
            case 3:
                zDirection = "+";
                xDirection = "+";
                break;
            case 4:
                zDirection = "++";
                break;
            case 5:
                zDirection = "+";
                xDirection = "-";
                break;
            case 6:
                xDirection = "--";
                break;
            case 7:
                zDirection = "-";
                xDirection = "-";
                break;
        }
        String facing = cardinalDirections[cardinalDirectionIndex];
        font.drawString(Integer.toString((int) x),
                font.drawString("X ", position.getX() + 4, position.getY() + 4, axisLabelColour.getValue(), shadow) - subtract,
                position.getY() + 4, axisValueColour.getValue(), shadow);

        if(xDirection != null && axisDirection) {
            font.drawString(xDirection, position.getX() + width - font.getStringWidth(xDirection) - 2,
                    position.getY() + 4,
                    axisDirectionColour.getValue(), shadow);
        }

        font.drawString(Integer.toString((int) y),
                font.drawString("Y ", position.getX() + 4, position.getY() + 4 + font.FONT_HEIGHT + 2,
                        axisLabelColour.getValue(),
                        shadow) - subtract,
                position.getY() + 4 + font.FONT_HEIGHT + 2, axisValueColour.getValue(), shadow);

        if(cardinalDirection) {
            font.drawString(facing, position.getX() + width - font.getStringWidth(facing) - 2,
                    position.getY() + 4 + font.FONT_HEIGHT + 2, cardinalDirectionColour.getValue(), shadow);
        }

        font.drawString(Integer.toString((int) z),
                font.drawString("Z ", position.getX() + 4,
                        position.getY() + 4 + font.FONT_HEIGHT + 2 + font.FONT_HEIGHT + 2,
                        axisLabelColour.getValue(), shadow) - subtract,
                position.getY() + 4 + font.FONT_HEIGHT + 2 + font.FONT_HEIGHT + 2, axisValueColour.getValue(), shadow);

        if(zDirection != null && axisDirection) {
            font.drawString(zDirection, position.getX() + width - font.getStringWidth(zDirection) - 2,
                    position.getY() + 4 + font.FONT_HEIGHT + 2 + font.FONT_HEIGHT + 2, axisDirectionColour.getValue(),
                    shadow);
        }

    }

}
