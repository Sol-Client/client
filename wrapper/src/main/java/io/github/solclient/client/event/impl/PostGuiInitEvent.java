package io.github.solclient.client.event.impl;

import java.util.Collection;

import lombok.AllArgsConstructor;
import net.minecraft.client.gui.*;

@AllArgsConstructor
public class PostGuiInitEvent {

	public final GuiScreen screen;
	public final Collection<GuiButton> buttonList;

}
