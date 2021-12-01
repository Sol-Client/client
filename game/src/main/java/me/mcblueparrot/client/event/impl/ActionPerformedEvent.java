package me.mcblueparrot.client.event.impl;

import lombok.RequiredArgsConstructor;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

@RequiredArgsConstructor
public class ActionPerformedEvent {

	public final GuiScreen gui;
	public final GuiButton button;
	public boolean cancelled;

}
