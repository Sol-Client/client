package io.github.solclient.client.event.impl;

import lombok.RequiredArgsConstructor;
import net.minecraft.client.gui.screen.Screen;

@RequiredArgsConstructor
public class PreGuiInitEvent {

	public final Screen screen;
	public boolean cancelled;

}
