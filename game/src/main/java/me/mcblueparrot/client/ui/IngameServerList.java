package me.mcblueparrot.client.ui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiScreenServerList;

import java.io.IOException;

public class IngameServerList extends GuiMultiplayer {

    public IngameServerList(GuiScreen parentScreen) {
        super(parentScreen);
    }

    @Override
    public void connectToSelected() {
        disconnect();
        super.connectToSelected();
    }

    @Override
    public void confirmClicked(boolean result, int id) {
        super.confirmClicked(result, id);
    }

    private void disconnect() {
        mc.theWorld.sendQuittingDisconnectingPacket();
        mc.loadWorld(null);
    }

}
