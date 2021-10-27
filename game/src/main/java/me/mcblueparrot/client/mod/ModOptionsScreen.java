package me.mcblueparrot.client.mod;//package me.mcblueparrot.client.mod;
//
//import java.awt.Color;
//import java.awt.Rectangle;
//
//import me.mcblueparrot.client.Client;
//import me.mcblueparrot.client.ui.Button;
//import me.mcblueparrot.client.util.Utils;
//import net.minecraft.client.gui.GuiScreen;
//
//public class ModOptionsScreen extends GuiScreen {
//
//    private Mod mod;
//    private boolean closeMethod;
//    private GuiScreen previous;
//    private boolean wasMouseDown;
//    private boolean mouseDown;
//
//    public ModOptionsScreen(Mod mod) {
//        this.mod = mod;
//    }
//
//    @Override
//    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
//        super.drawScreen(mouseX, mouseY, partialTicks);
//        if(mc.theWorld == null) {
//            drawRect(0, 0, width, height, new Color(20, 20, 20).getRGB());
//        }
//        else {
//            drawWorldBackground(0);
//        }
//        fontRendererObj.drawString(mod.getName(), (width / 2) - (fontRendererObj.getStringWidth(mod.getName()) / 2), 15, Utils.WHITE);
//
//        Button done = new Button("Done", new Rectangle(width / 2 - 50, height - 25, 100, 20), new Color(0, 100, 0), new Color(0, 120, 0));
//        done.render(mouseX, mouseY);
//        if(done.isHovered(mouseX, mouseY) && mouseDown && !wasMouseDown) {
//            Utils.playClickSound();
//            mc.displayGuiScreen(previous);
//        }
//
//        wasMouseDown = mouseDown;
//    }
//
//    @Override
//    public void onGuiClosed() {
//        super.onGuiClosed();
//        if(!closeMethod) {
//            Client.INSTANCE.save();
//        }
//    }
//
//}
//
//
