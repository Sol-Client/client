//package me.mcblueparrot.client.util;
//
//import java.awt.Rectangle;
//import java.util.Arrays;
//import java.util.List;
//
//import net.minecraft.client.Minecraft;
//import net.minecraft.client.gui.GuiButton;
//
//public class ColourButton extends GuiButton {
//
////    private List<Colour> colours;
////    private Colour colour;
////
////    public ColourButton(int buttonId, int x, int y, ColourSelection selection) {
////        super(buttonId, x, y, 20, 20, "");
////        colours = Arrays.asList(selection.colours);
////        colour = colours.get(0);
////    }
////
////    @Override
////    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
////        drawRect(xPosition, yPosition, xPosition + width, yPosition + height, colour.getRGB());
////        Utils.drawOutline(new Rectangle(xPosition, yPosition, width, height), new Colour( -6250336), true);
////    }
////
////    public Colour setColour(Colour color) {
////        if(color != null) {
////            return this.colour = color;
////        }
////        return null;
////    }
////
////    public void cycleColour() {
////        if(colours.contains(colour) && colours.indexOf(colour) != colours.size() - 1) {
////            colour = colours.get(colours.indexOf(colour) + 1);
////        }
////        else {
////            colour = colours.get(0);
////        }
////    }
////
////    public Colour getColour() {
////        return colour;
////    }
////
////    public enum ColourSelection {
////        BACKGROUND(new Colour(0, 0, 0, 100), new Colour(255, 255, 255, 100)),
////        FOREGROUND(Colour.WHITE, Colour.BLACK, Colour.RED, Colour.GREEN, Colour.BLUE, Colour.ORANGE, Colour.YELLOW, Colour.MAGENTA),
////        NUMBERS(new Colour(553648127));
////
////        private Colour[] colours;
////
////        private ColourSelection(Colour... colours) {
////            this.colours = colours;
////        }
////
////        public Colour[] getColours() {
////            return colours;
////        }
////
////    }
//
//}
