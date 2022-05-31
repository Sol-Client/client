package io.github.solclient.client.event.impl;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MouseClickEvent {

	public final int button;
	public boolean cancelled;

}
