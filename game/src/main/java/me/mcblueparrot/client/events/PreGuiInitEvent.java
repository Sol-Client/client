package me.mcblueparrot.client.events;

import lombok.RequiredArgsConstructor;
import net.minecraft.client.gui.GuiScreen;

@RequiredArgsConstructor
public class PreGuiInitEvent {

	public final GuiScreen screen;
	public boolean cancelled;

}
