package io.github.solclient.client.event.impl;

import lombok.RequiredArgsConstructor;
import net.minecraft.client.gui.GuiScreen;

@RequiredArgsConstructor
public class PreGuiInitEvent {

	public final GuiScreen screen;
	public boolean cancelled;

}
