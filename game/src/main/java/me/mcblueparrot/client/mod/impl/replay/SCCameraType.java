package me.mcblueparrot.client.mod.impl.replay;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum SCCameraType {
	CLASSIC("Classic"),
	VANILLA_ISH("Vanilla-ish");

	private String name;

	@Override
	public String toString() {
		return name;
	}
}