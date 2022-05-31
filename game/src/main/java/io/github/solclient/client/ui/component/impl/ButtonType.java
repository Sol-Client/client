package io.github.solclient.client.ui.component.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum ButtonType {
	SMALL(20),
	NORMAL(100),
	LARGE(200);

	@Getter
	private final int width;

	@Override
	public String toString() {
		return super.toString().toLowerCase();
	}

}
