package me.mcblueparrot.client.events;

import net.minecraft.client.gui.GuiScreen;

public class OpenGuiEvent {

    public GuiScreen screen;

    public OpenGuiEvent(GuiScreen screen) {
        this.screen = screen;
    }

}
