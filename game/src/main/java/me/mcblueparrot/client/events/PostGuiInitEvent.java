package me.mcblueparrot.client.events;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import java.util.Collection;

@AllArgsConstructor
public class PostGuiInitEvent {

    public GuiScreen screen;
    public final Collection<GuiButton> buttonList;

}
