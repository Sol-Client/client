package me.mcblueparrot.client.event.impl;

import java.util.Collection;

import lombok.AllArgsConstructor;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

@AllArgsConstructor
public class PostGuiInitEvent {

	public GuiScreen screen;
	public final Collection<GuiButton> buttonList;

}
