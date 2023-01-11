package io.github.solclient.client.event.impl;

import lombok.RequiredArgsConstructor;
import net.minecraft.client.gui.*;

@RequiredArgsConstructor
public class ActionPerformedEvent {

	public final GuiScreen gui;
	public final GuiButton button;
	public boolean cancelled;

}
