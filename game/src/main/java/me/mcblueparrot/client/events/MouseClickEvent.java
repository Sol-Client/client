package me.mcblueparrot.client.events;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MouseClickEvent {

	public final int button;
	public boolean cancelled;

}
