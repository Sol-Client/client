package io.github.solclient.client.event.impl;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class KeyPressEvent {

	public final int key;
	public boolean cancelled;

}
