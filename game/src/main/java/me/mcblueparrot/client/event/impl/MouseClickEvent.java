package me.mcblueparrot.client.event.impl;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MouseClickEvent {

	public final int button;
	public boolean cancelled;

}
